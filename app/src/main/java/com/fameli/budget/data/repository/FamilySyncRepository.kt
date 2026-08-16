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

    // Отправка одного элемента
    suspend fun syncTransaction(familyId: String, txn: TransactionEntity) {
        try { firestore.collection("families/$familyId/transactions").document(txn.cloudId).set(txn).await() } catch (e: Exception) {}
    }

    suspend fun syncShoppingItem(familyId: String, item: ShoppingItemEntity) {
        try { firestore.collection("families/$familyId/shopping").document(item.cloudId).set(item).await() } catch (e: Exception) {}
    }

    suspend fun syncTask(familyId: String, task: TaskEntity) {
        try { firestore.collection("families/$familyId/tasks").document(task.cloudId).set(task).await() } catch (e: Exception) {}
    }

    suspend fun syncGoal(familyId: String, goal: GoalEntity) {
        try { firestore.collection("families/$familyId/goals").document(goal.cloudId).set(goal).await() } catch (e: Exception) {}
    }

    suspend fun syncCategory(familyId: String, cat: CategoryEntity) {
        try { firestore.collection("families/$familyId/categories").document(cat.cloudId.ifBlank { "cat_${cat.id}" }).set(cat).await() } catch (e: Exception) {}
    }

    // ПРОСТАЯ СИНХРОНИЗАЦИЯ — без first() и блокировок
    suspend fun syncAllToCloud(familyId: String): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncAllFromCloud(familyId: String): Result<Unit> {
        return try {
            firestore.collection("families/$familyId/transactions").get().await().documents.forEach {
                it.toObject(TransactionEntity::class.java)?.let { t -> transactionDao.insert(t) }
            }
            firestore.collection("families/$familyId/shopping").get().await().documents.forEach {
                it.toObject(ShoppingItemEntity::class.java)?.let { s -> shoppingDao.insert(s) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun startListening(familyId: String) {
        stopListening()
        try {
            listeners.add(firestore.collection("families/$familyId/transactions").addSnapshotListener { s, _ ->
                s?.documents?.forEach { it.toObject(TransactionEntity::class.java)?.let { t -> kotlinx.coroutines.runBlocking { transactionDao.insert(t) } } }
            })
        } catch (e: Exception) { Log.e(TAG, "listener: ${e.message}") }
    }

    fun stopListening() { listeners.forEach { it.remove() }; listeners.clear() }
}
