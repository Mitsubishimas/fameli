package com.fameli.budget.data.repository

import android.util.Log
import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.first
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

    // ==== АВТОМАТИЧЕСКАЯ ОТПРАВКА В ОБЛАКО ====
    suspend fun syncTransaction(familyId: String, txn: TransactionEntity) {
        try { firestore.collection("families/$familyId/transactions").document(txn.cloudId).set(txn).await() }
        catch (e: Exception) { Log.e(TAG, "txn sync: ${e.message}") }
    }

    suspend fun syncCategory(familyId: String, cat: CategoryEntity) {
        try { firestore.collection("families/$familyId/categories").document(cat.cloudId.ifBlank { "cat_${cat.id}" }).set(cat).await() }
        catch (e: Exception) { Log.e(TAG, "cat sync: ${e.message}") }
    }

    suspend fun syncTask(familyId: String, task: TaskEntity) {
        try { firestore.collection("families/$familyId/tasks").document(task.cloudId.ifBlank { "task_${task.id}" }).set(task).await() }
        catch (e: Exception) { Log.e(TAG, "task sync: ${e.message}") }
    }

    suspend fun syncGoal(familyId: String, goal: GoalEntity) {
        try { firestore.collection("families/$familyId/goals").document(goal.cloudId.ifBlank { "goal_${goal.id}" }).set(goal).await() }
        catch (e: Exception) { Log.e(TAG, "goal sync: ${e.message}") }
    }

    suspend fun syncShoppingItem(familyId: String, item: ShoppingItemEntity) {
        try { firestore.collection("families/$familyId/shopping").document(item.cloudId.ifBlank { "shop_${item.id}" }).set(item).await() }
        catch (e: Exception) { Log.e(TAG, "shop sync: ${e.message}") }
    }

    // ==== ПОЛНАЯ СИНХРОНИЗАЦИЯ ====
    suspend fun syncAllToCloud(familyId: String): Result<Unit> = try {
        val txn = transactionDao.getAll().first().filter { it.cloudId.isNotEmpty() }
        txn.forEach { firestore.collection("families/$familyId/transactions").document(it.cloudId).set(it).await() }

        val cats = categoryDao.getAll().first()
        cats.forEach { firestore.collection("families/$familyId/categories").document(it.cloudId.ifBlank { "cat_${it.id}" }).set(it).await() }

        val tasks = taskDao.getAll().first()
        tasks.forEach { firestore.collection("families/$familyId/tasks").document(it.cloudId.ifBlank { "task_${it.id}" }).set(it).await() }

        val goals = goalDao.getAll().first()
        goals.forEach { firestore.collection("families/$familyId/goals").document(it.cloudId.ifBlank { "goal_${it.id}" }).set(it).await() }

        val shop = shoppingDao.getAll().first()
        shop.forEach { firestore.collection("families/$familyId/shopping").document(it.cloudId.ifBlank { "shop_${it.id}" }).set(it).await() }

        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun syncAllFromCloud(familyId: String): Result<Unit> = try {
        firestore.collection("families/$familyId/transactions").get().await().documents.forEach {
            it.toObject(TransactionEntity::class.java)?.let { t ->
                if (transactionDao.getByCloudId(t.cloudId) == null) transactionDao.insert(t)
            }
        }
        firestore.collection("families/$familyId/categories").get().await().documents.forEach {
            it.toObject(CategoryEntity::class.java)?.let { c -> categoryDao.insert(c) }
        }
        firestore.collection("families/$familyId/tasks").get().await().documents.forEach {
            it.toObject(TaskEntity::class.java)?.let { t -> taskDao.insert(t) }
        }
        firestore.collection("families/$familyId/goals").get().await().documents.forEach {
            it.toObject(GoalEntity::class.java)?.let { g -> goalDao.insertGoal(g) }
        }
        firestore.collection("families/$familyId/shopping").get().await().documents.forEach {
            it.toObject(ShoppingItemEntity::class.java)?.let { s -> shoppingDao.insert(s) }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    // ==== СЛУШАТЕЛИ ====
    fun startListening(familyId: String) {
        stopListening()
        try {
            listeners.add(firestore.collection("families/$familyId/transactions").addSnapshotListener { s, e ->
                s?.documents?.forEach { it.toObject(TransactionEntity::class.java)?.let { t ->
                    kotlinx.coroutines.runBlocking { if (transactionDao.getByCloudId(t.cloudId) == null) transactionDao.insert(t) }
                } }
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
        } catch (e: Exception) { Log.e(TAG, "listener: ${e.message}") }
    }

    fun stopListening() { listeners.forEach { it.remove() }; listeners.clear() }
}
