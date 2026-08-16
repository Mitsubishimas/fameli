package com.fameli.budget.data.repository

import android.util.Log
import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    private val shoppingDao: ShoppingDao,
    private val familyManager: FamilyManager
) {
    companion object { private const val TAG = "FAMILY_SYNC" }
    private var listeners: MutableList<ListenerRegistration> = mutableListOf()

    suspend fun createFamily(familyName: String): Result<String> = try {
        val userId = authRepository.getUserId() ?: throw Exception("Не авторизован")
        val ref = firestore.collection("families").document()
        ref.set(mapOf("name" to familyName, "createdBy" to userId, "createdAt" to System.currentTimeMillis(), "members" to listOf(userId))).await()
        familyManager.currentFamilyId = ref.id
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
        familyManager.currentFamilyId = familyId
        Result.success(true)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getMyFamilies(): List<String> = try {
        val userId = authRepository.getUserId() ?: return emptyList()
        firestore.collection("families").whereArrayContains("members", userId).get().await().documents.map { it.id }
    } catch (e: Exception) { emptyList() }

    // Отправка в облако
    suspend fun syncTransaction(txn: TransactionEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/transactions").document(txn.cloudId).set(txn).await() } catch (e: Exception) {}
    }

    suspend fun syncShoppingItem(item: ShoppingItemEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/shopping").document(item.cloudId).set(item).await() } catch (e: Exception) {}
    }

    suspend fun syncTask(task: TaskEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/tasks").document(task.cloudId).set(task).await() } catch (e: Exception) {}
    }

    suspend fun syncGoal(goal: GoalEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/goals").document(goal.cloudId).set(goal).await() } catch (e: Exception) {}
    }

    suspend fun syncCategory(cat: CategoryEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/categories").document(cat.cloudId.ifBlank { "cat_${cat.id}" }).set(cat).await() } catch (e: Exception) {}
    }

    // Загрузка из облака
    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            val txnSnap = firestore.collection("families/$fid/transactions").get().await()
            txnSnap.documents.forEach { doc ->
                doc.toObject(TransactionEntity::class.java)?.let { transactionDao.insert(it) }
            }

            val shopSnap = firestore.collection("families/$fid/shopping").get().await()
            shopSnap.documents.forEach { doc ->
                doc.toObject(ShoppingItemEntity::class.java)?.let { shoppingDao.insert(it) }
            }

            val taskSnap = firestore.collection("families/$fid/tasks").get().await()
            taskSnap.documents.forEach { doc ->
                doc.toObject(TaskEntity::class.java)?.let { taskDao.insert(it) }
            }

            val goalSnap = firestore.collection("families/$fid/goals").get().await()
            goalSnap.documents.forEach { doc ->
                doc.toObject(GoalEntity::class.java)?.let { goalDao.insertGoal(it) }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "syncAllFromCloud: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun startListening() {
        stopListening()
        val fid = familyManager.currentFamilyId ?: return
        try {
            listeners.add(firestore.collection("families/$fid/transactions").addSnapshotListener { s, _ ->
                s?.documents?.forEach { it.toObject(TransactionEntity::class.java)?.let { t -> kotlinx.coroutines.runBlocking { transactionDao.insert(t) } } }
            })
        } catch (e: Exception) { Log.e(TAG, "listener: ${e.message}") }
    }

    fun stopListening() { listeners.forEach { it.remove() }; listeners.clear() }
}
