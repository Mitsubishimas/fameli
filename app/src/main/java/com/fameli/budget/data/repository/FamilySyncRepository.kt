package com.fameli.budget.data.repository

import android.util.Log
import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilySyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: FirebaseAuthRepository,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
    private val goalDao: GoalDao,
    private val shoppingDao: ShoppingDao
) {
    companion object { private const val TAG = "FAMILY_SYNC" }
    private var listeners: MutableList<ListenerRegistration> = mutableListOf()

    suspend fun createFamily(familyName: String): Result<String> = try {
        val userId = authRepository.getUserId() ?: throw Exception("Не авторизован")
        val ref = firestore.collection("families").document()
        ref.set(mapOf("name" to familyName, "createdBy" to userId, "createdAt" to System.currentTimeMillis(), "members" to listOf(userId))).await()
        Result.success(ref.id)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun joinFamily(familyId: String): Result<Boolean> = try {
        val userId = authRepository.getUserId() ?: throw Exception("Не авторизован")
        val doc = firestore.collection("families").document(familyId).get().await()
        if (!doc.exists()) throw Exception("Семья не найдена")
        val members = (doc.get("members") as? List<*>)?.toMutableList() ?: mutableListOf()
        if (!members.contains(userId)) {
            members.add(userId)
            firestore.collection("families").document(familyId).update("members", members).await()
        }
        Result.success(true)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getMyFamilies(): List<String> = try {
        val userId = authRepository.getUserId() ?: return emptyList()
        firestore.collection("families").whereArrayContains("members", userId).get().await().documents.map { it.id }
    } catch (e: Exception) { emptyList() }

    // Принудительная синхронизация: локальные данные -> облако
    suspend fun forceSyncToCloud(familyId: String): Result<Unit> = try {
        val txn = transactionDao.getAll().first().filter { it.cloudId.isNotEmpty() }
        txn.forEach { firestore.collection("families/$familyId/transactions").document(it.cloudId).set(it).await() }

        val cats = categoryDao.getAll().first().filter { it.cloudId.isNotEmpty() }
        cats.forEach { firestore.collection("families/$familyId/categories").document(it.cloudId).set(it).await() }

        val tasks = taskDao.getAll().first().filter { it.cloudId.isNotEmpty() }
        tasks.forEach { firestore.collection("families/$familyId/tasks").document(it.cloudId).set(it).await() }

        val goals = goalDao.getAll().first().filter { it.cloudId.isNotEmpty() }
        goals.forEach { firestore.collection("families/$familyId/goals").document(it.cloudId).set(it).await() }

        val shop = shoppingDao.getAll().first().filter { it.cloudId.isNotEmpty() }
        shop.forEach { firestore.collection("families/$familyId/shopping").document(it.cloudId).set(it).await() }

        Log.d(TAG, "Принудительная синхронизация завершена")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Ошибка синхронизации: ${e.message}", e)
        Result.failure(e)
    }

    // Принудительная синхронизация: облако -> локально
    suspend fun forceSyncFromCloud(familyId: String): Result<Unit> = try {
        val txn = firestore.collection("families/$familyId/transactions").get().await()
        txn.documents.forEach { doc -> doc.toObject(TransactionEntity::class.java)?.let { transactionDao.insert(it) } }

        val cats = firestore.collection("families/$familyId/categories").get().await()
        cats.documents.forEach { doc -> doc.toObject(CategoryEntity::class.java)?.let { categoryDao.insert(it) } }

        val tasks = firestore.collection("families/$familyId/tasks").get().await()
        tasks.documents.forEach { doc -> doc.toObject(TaskEntity::class.java)?.let { taskDao.insert(it) } }

        val goals = firestore.collection("families/$familyId/goals").get().await()
        goals.documents.forEach { doc -> doc.toObject(GoalEntity::class.java)?.let { goalDao.insertGoal(it) } }

        val shop = firestore.collection("families/$familyId/shopping").get().await()
        shop.documents.forEach { doc -> doc.toObject(ShoppingItemEntity::class.java)?.let { shoppingDao.insert(it) } }

        Log.d(TAG, "Загрузка из облака завершена")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Ошибка загрузки: ${e.message}", e)
        Result.failure(e)
    }

    fun startListening(familyId: String) {
        stopListening()
        try {
            listeners.add(firestore.collection("families/$familyId/transactions").addSnapshotListener { s, e ->
                s?.documents?.forEach { it.toObject(TransactionEntity::class.java)?.let { t -> kotlinx.coroutines.runBlocking { transactionDao.insert(t) } } }
            })
            listeners.add(firestore.collection("families/$familyId/categories").addSnapshotListener { s, e ->
                s?.documents?.forEach { it.toObject(CategoryEntity::class.java)?.let { c -> kotlinx.coroutines.runBlocking { categoryDao.insert(c) } } }
            })
            listeners.add(firestore.collection("families/$familyId/tasks").addSnapshotListener { s, e ->
                s?.documents?.forEach { it.toObject(TaskEntity::class.java)?.let { t -> kotlinx.coroutines.runBlocking { taskDao.insert(t) } } }
            })
            listeners.add(firestore.collection("families/$familyId/goals").addSnapshotListener { s, e ->
                s?.documents?.forEach { it.toObject(GoalEntity::class.java)?.let { g -> kotlinx.coroutines.runBlocking { goalDao.insertGoal(g) } } }
            })
            listeners.add(firestore.collection("families/$familyId/shopping").addSnapshotListener { s, e ->
                s?.documents?.forEach { it.toObject(ShoppingItemEntity::class.java)?.let { i -> kotlinx.coroutines.runBlocking { shoppingDao.insert(i) } } }
            })
        } catch (e: Exception) { Log.e(TAG, "Listener error: ${e.message}") }
    }

    fun stopListening() { listeners.forEach { it.remove() }; listeners.clear() }
}

// Дополнительные методы для синхронизации отдельных элементов
suspend fun syncTransaction(familyId: String, txn: TransactionEntity) {
    try {
        firestore.collection("families/$familyId/transactions").document(txn.cloudId).set(txn).await()
    } catch (e: Exception) {
        Log.e("FAMILY_SYNC", "syncTransaction error: ${e.message}")
    }
}

suspend fun syncShoppingItem(familyId: String, item: ShoppingItemEntity) {
    try {
        firestore.collection("families/$familyId/shopping").document(item.cloudId).set(item).await()
    } catch (e: Exception) {
        Log.e("FAMILY_SYNC", "syncShoppingItem error: ${e.message}")
    }
}
