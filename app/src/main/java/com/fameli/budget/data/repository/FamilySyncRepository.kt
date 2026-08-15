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

    suspend fun syncTransaction(familyId: String, txn: TransactionEntity) {
        try {
            // Проверяем нет ли уже такой транзакции
            val existing = transactionDao.getByCloudId(txn.cloudId)
            if (existing == null) {
                transactionDao.insert(txn)
                firestore.collection("families/$familyId/transactions").document(txn.cloudId).set(txn).await()
            }
        } catch (e: Exception) { Log.e(TAG, "txn sync: ${e.message}") }
    }

    fun startListening(familyId: String) {
        stopListening()
        try {
            listeners.add(firestore.collection("families/$familyId/transactions").addSnapshotListener { s, e ->
                if (e != null) return@addSnapshotListener
                s?.documents?.forEach { doc ->
                    val txn = doc.toObject(TransactionEntity::class.java)
                    if (txn != null) {
                        kotlinx.coroutines.runBlocking {
                            // Вставляем только если нет дубликата
                            val existing = transactionDao.getByCloudId(txn.cloudId)
                            if (existing == null) {
                                transactionDao.insert(txn)
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) { Log.e(TAG, "listener: ${e.message}") }
    }

    fun stopListening() { listeners.forEach { it.remove() }; listeners.clear() }

    suspend fun syncShoppingItem(familyId: String, item: ShoppingItemEntity) {
        try { firestore.collection("families/$familyId/shopping").document(item.cloudId).set(item).await() }
        catch (e: Exception) { Log.e(TAG, "shop sync: ${e.message}") }
    }

    suspend fun forceSyncToCloud(familyId: String): Result<Unit> = try {
        val txn = transactionDao.getAll().first()
        txn.forEach { firestore.collection("families/$familyId/transactions").document(it.cloudId.ifBlank { "t_${it.localId}" }).set(it).await() }
        val shop = shoppingDao.getAll().first()
        shop.forEach { firestore.collection("families/$familyId/shopping").document(it.cloudId.ifBlank { "s_${it.id}" }).set(it).await() }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun forceSyncFromCloud(familyId: String): Result<Unit> = try {
        firestore.collection("families/$familyId/transactions").get().await().documents.forEach { it.toObject(TransactionEntity::class.java)?.let { t -> transactionDao.insert(t) } }
        firestore.collection("families/$familyId/shopping").get().await().documents.forEach { it.toObject(ShoppingItemEntity::class.java)?.let { s -> shoppingDao.insert(s) } }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
