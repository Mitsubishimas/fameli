package com.fameli.budget.data.repository

import android.util.Log
import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
        if (!members.contains(userId)) { members.add(userId); firestore.collection("families").document(familyId).update("members", members).await() }
        familyManager.currentFamilyId = familyId
        Result.success(true)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getMyFamilies(): List<String> = try {
        val userId = authRepository.getUserId() ?: return emptyList()
        firestore.collection("families").whereArrayContains("members", userId).get().await().documents.map { it.id }
    } catch (e: Exception) { emptyList() }

    suspend fun syncTransaction(txn: TransactionEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/transactions").document(txn.cloudId).set(txn).await() } catch (e: Exception) { Log.e(TAG, "txn: ${e.message}") }
    }
    suspend fun syncShoppingItem(item: ShoppingItemEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/shopping").document(item.cloudId).set(item).await() } catch (e: Exception) { Log.e(TAG, "shop: ${e.message}") }
    }
    suspend fun syncTask(task: TaskEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/tasks").document(task.cloudId).set(task).await() } catch (e: Exception) { Log.e(TAG, "task: ${e.message}") }
    }
    suspend fun syncGoal(goal: GoalEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/goals").document(goal.cloudId).set(goal).await() } catch (e: Exception) { Log.e(TAG, "goal: ${e.message}") }
    }
    suspend fun syncCategory(cat: CategoryEntity) {
        val fid = familyManager.currentFamilyId ?: return
        try { firestore.collection("families/$fid/categories").document(cat.cloudId.ifBlank { "cat_${cat.id}" }).set(cat).await() } catch (e: Exception) { Log.e(TAG, "cat: ${e.message}") }
    }

    suspend fun syncAllLocalToCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            transactionDao.getAll().first().forEach { syncTransaction(it) }
            shoppingDao.getAll().first().forEach { syncShoppingItem(it) }
            taskDao.getAll().first().forEach { syncTask(it) }
            goalDao.getAll().first().forEach { syncGoal(it) }
            categoryDao.getAll().first().forEach { syncCategory(it) }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            firestore.collection("families/$fid/categories").get().await().documents.forEach { doc ->
                doc.toObject(CategoryEntity::class.java)?.let { cat ->
                    if (categoryDao.getByCloudId(cat.cloudId) == null) categoryDao.insert(cat)
                }
            }
            firestore.collection("families/$fid/transactions").get().await().documents.forEach { doc ->
                doc.toObject(TransactionEntity::class.java)?.let { txn ->
                    if (transactionDao.getByCloudId(txn.cloudId) == null) transactionDao.insert(txn)
                }
            }
            firestore.collection("families/$fid/shopping").get().await().documents.forEach { doc ->
                doc.toObject(ShoppingItemEntity::class.java)?.let { item ->
                    if (shoppingDao.getByCloudId(item.cloudId) == null) shoppingDao.insert(item)
                }
            }
            firestore.collection("families/$fid/tasks").get().await().documents.forEach { doc ->
                doc.toObject(TaskEntity::class.java)?.let { task ->
                    if (taskDao.getByCloudId(task.cloudId) == null) taskDao.insert(task)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    fun startListening() {
        stopListening()
        val fid = familyManager.currentFamilyId ?: return
        try {
            listeners.add(firestore.collection("families/$fid/categories").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc ->
                    try {
                        doc.toObject(CategoryEntity::class.java)?.let { cat ->
                            kotlinx.coroutines.runBlocking {
                                if (categoryDao.getByCloudId(cat.cloudId) == null) categoryDao.insert(cat)
                            }
                        }
                    } catch (ex: Exception) {}
                }
            })
            listeners.add(firestore.collection("families/$fid/transactions").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc ->
                    try {
                        doc.toObject(TransactionEntity::class.java)?.let { txn ->
                            kotlinx.coroutines.runBlocking {
                                if (transactionDao.getByCloudId(txn.cloudId) == null) transactionDao.insert(txn)
                            }
                        }
                    } catch (ex: Exception) {}
                }
            })
            listeners.add(firestore.collection("families/$fid/shopping").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc ->
                    try {
                        doc.toObject(ShoppingItemEntity::class.java)?.let { item ->
                            kotlinx.coroutines.runBlocking {
                                val existing = shoppingDao.getByCloudId(item.cloudId)
                                if (existing == null) shoppingDao.insert(item)
                                else shoppingDao.update(item.copy(id = existing.id))
                            }
                        }
                    } catch (ex: Exception) {}
                }
            })
            listeners.add(firestore.collection("families/$fid/tasks").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc ->
                    try {
                        doc.toObject(TaskEntity::class.java)?.let { task ->
                            kotlinx.coroutines.runBlocking {
                                val existing = taskDao.getByCloudId(task.cloudId)
                                if (existing == null) taskDao.insert(task)
                                else taskDao.update(task.copy(id = existing.id))
                            }
                        }
                    } catch (ex: Exception) {}
                }
            })
        } catch (e: Exception) { Log.e(TAG, "listener: ${e.message}") }
    }

    fun stopListening() { listeners.forEach { it.remove() }; listeners.clear() }
}
