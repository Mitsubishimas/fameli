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
import java.util.UUID
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

    // ========== ОТПРАВКА В ОБЛАКО ==========
    suspend fun syncTransaction(txn: TransactionEntity) {
        val fid = familyManager.currentFamilyId ?: return
        val docId = txn.cloudId.ifBlank { UUID.randomUUID().toString() }
        try { firestore.collection("families/$fid/transactions").document(docId).set(txn.copy(cloudId = docId, lastModified = System.currentTimeMillis())).await() } catch (e: Exception) { Log.e(TAG, "txn: ${e.message}") }
    }

    suspend fun syncShoppingItem(item: ShoppingItemEntity) {
        val fid = familyManager.currentFamilyId ?: return
        val docId = item.cloudId.ifBlank { UUID.randomUUID().toString() }
        try { firestore.collection("families/$fid/shopping").document(docId).set(item.copy(cloudId = docId)).await() } catch (e: Exception) { Log.e(TAG, "shop: ${e.message}") }
    }

    suspend fun syncTask(task: TaskEntity) {
        val fid = familyManager.currentFamilyId ?: return
        val docId = task.cloudId.ifBlank { UUID.randomUUID().toString() }
        try { firestore.collection("families/$fid/tasks").document(docId).set(task.copy(cloudId = docId, lastModified = System.currentTimeMillis())).await() } catch (e: Exception) { Log.e(TAG, "task: ${e.message}") }
    }

    suspend fun syncGoal(goal: GoalEntity) {
        val fid = familyManager.currentFamilyId ?: return
        val docId = goal.cloudId.ifBlank { UUID.randomUUID().toString() }
        try { firestore.collection("families/$fid/goals").document(docId).set(goal.copy(cloudId = docId, lastModified = System.currentTimeMillis())).await() } catch (e: Exception) { Log.e(TAG, "goal: ${e.message}") }
    }

    suspend fun syncCategory(cat: CategoryEntity) {
        val fid = familyManager.currentFamilyId ?: return
        val docId = cat.cloudId.ifBlank { "cat_${cat.id}" }
        try { firestore.collection("families/$fid/categories").document(docId).set(cat.copy(cloudId = docId)).await() } catch (e: Exception) { Log.e(TAG, "cat: ${e.message}") }
    }

    // ========== ОТПРАВИТЬ ВСЁ ЛОКАЛЬНОЕ ==========
    suspend fun syncAllLocalToCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            categoryDao.getAll().first().forEach { syncCategory(it) }
            transactionDao.getAll().first().forEach { syncTransaction(it) }
            shoppingDao.getAll().first().forEach { syncShoppingItem(it) }
            taskDao.getAll().first().forEach { syncTask(it) }
            goalDao.getAll().first().forEach { syncGoal(it) }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // ========== ЗАГРУЗКА ИЗ ОБЛАКА (UPSERT с lastModified) ==========
    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            // Категории
            firestore.collection("families/$fid/categories").get().await().documents.forEach { doc ->
                val cloudCat = doc.toObject(CategoryEntity::class.java) ?: return@forEach
                val localCat = categoryDao.getByCloudId(cloudCat.cloudId)
                if (localCat == null) categoryDao.insert(cloudCat)
                else if (cloudCat.lastModified > localCat.lastModified) categoryDao.update(cloudCat.copy(id = localCat.id))
            }

            // Транзакции
            firestore.collection("families/$fid/transactions").get().await().documents.forEach { doc ->
                val cloudTxn = doc.toObject(TransactionEntity::class.java) ?: return@forEach
                val localTxn = transactionDao.getByCloudId(cloudTxn.cloudId)
                if (localTxn == null) transactionDao.insert(cloudTxn)
                else if (cloudTxn.lastModified > localTxn.lastModified) transactionDao.update(cloudTxn.copy(localId = localTxn.localId))
            }

            // Покупки
            firestore.collection("families/$fid/shopping").get().await().documents.forEach { doc ->
                val cloudItem = doc.toObject(ShoppingItemEntity::class.java) ?: return@forEach
                val localItem = shoppingDao.getByCloudId(cloudItem.cloudId)
                if (localItem == null) shoppingDao.insert(cloudItem)
                else shoppingDao.update(cloudItem.copy(id = localItem.id))
            }

            // Задачи
            firestore.collection("families/$fid/tasks").get().await().documents.forEach { doc ->
                val cloudTask = doc.toObject(TaskEntity::class.java) ?: return@forEach
                val localTask = taskDao.getByCloudId(cloudTask.cloudId)
                if (localTask == null) taskDao.insert(cloudTask)
                else if (cloudTask.lastModified > localTask.lastModified) taskDao.update(cloudTask.copy(id = localTask.id))
            }

            // Цели
            firestore.collection("families/$fid/goals").get().await().documents.forEach { doc ->
                val cloudGoal = doc.toObject(GoalEntity::class.java) ?: return@forEach
                val localGoal = goalDao.getByCloudId(cloudGoal.cloudId)
                if (localGoal == null) goalDao.insertGoal(cloudGoal)
                else if (cloudGoal.lastModified > localGoal.lastModified) goalDao.updateGoal(cloudGoal.copy(id = localGoal.id))
            }

            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // ========== СЛУШАТЕЛИ ==========
    fun startListening() {
        stopListening()
        val fid = familyManager.currentFamilyId ?: return
        try {
            listeners.add(firestore.collection("families/$fid/categories").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc -> try {
                    doc.toObject(CategoryEntity::class.java)?.let { cat -> kotlinx.coroutines.runBlocking {
                        val local = categoryDao.getByCloudId(cat.cloudId)
                        if (local == null) categoryDao.insert(cat)
                        else if (cat.lastModified > local.lastModified) categoryDao.update(cat.copy(id = local.id))
                    } }
                } catch (ex: Exception) {} }
            })
            listeners.add(firestore.collection("families/$fid/transactions").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc -> try {
                    doc.toObject(TransactionEntity::class.java)?.let { txn -> kotlinx.coroutines.runBlocking {
                        val local = transactionDao.getByCloudId(txn.cloudId)
                        if (local == null) transactionDao.insert(txn)
                        else if (txn.lastModified > local.lastModified) transactionDao.update(txn.copy(localId = local.localId))
                    } }
                } catch (ex: Exception) {} }
            })
            listeners.add(firestore.collection("families/$fid/shopping").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc -> try {
                    doc.toObject(ShoppingItemEntity::class.java)?.let { item -> kotlinx.coroutines.runBlocking {
                        val local = shoppingDao.getByCloudId(item.cloudId)
                        if (local == null) shoppingDao.insert(item)
                        else shoppingDao.update(item.copy(id = local.id))
                    } }
                } catch (ex: Exception) {} }
            })
            listeners.add(firestore.collection("families/$fid/tasks").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc -> try {
                    doc.toObject(TaskEntity::class.java)?.let { task -> kotlinx.coroutines.runBlocking {
                        val local = taskDao.getByCloudId(task.cloudId)
                        if (local == null) taskDao.insert(task)
                        else if (task.lastModified > local.lastModified) taskDao.update(task.copy(id = local.id))
                    } }
                } catch (ex: Exception) {} }
            })
        } catch (e: Exception) { Log.e(TAG, "listener: ${e.message}") }
    }

    fun stopListening() { listeners.forEach { it.remove() }; listeners.clear() }
}
