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
    companion object {
        private const val TAG = "FAMILY_SYNC"
    }

    private var listeners: MutableList<ListenerRegistration> = mutableListOf()

    suspend fun createFamily(familyName: String): Result<String> {
        return try {
            val userId = authRepository.getUserId() ?: throw Exception("Не авторизован")
            val familyRef = firestore.collection("families").document()
            val data = mapOf(
                "name" to familyName,
                "createdBy" to userId,
                "createdAt" to System.currentTimeMillis(),
                "members" to listOf(userId)
            )
            familyRef.set(data).await()
            Log.d(TAG, "Семья создана: ${familyRef.id}")
            Result.success(familyRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun joinFamily(familyId: String): Result<Boolean> {
        return try {
            val userId = authRepository.getUserId() ?: throw Exception("Не авторизован")
            val doc = firestore.collection("families").document(familyId).get().await()
            if (!doc.exists()) throw Exception("Семья не найдена")
            
            val members = (doc.get("members") as? List<*>)?.toMutableList() ?: mutableListOf()
            if (!members.contains(userId)) {
                members.add(userId)
                firestore.collection("families").document(familyId)
                    .update("members", members)
                    .await()
            }
            Log.d(TAG, "Присоединился: $familyId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка присоединения: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getMyFamilies(): List<String> {
        return try {
            val userId = authRepository.getUserId() ?: return emptyList()
            val snapshot = firestore.collection("families")
                .whereArrayContains("members", userId)
                .get()
                .await()
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun startListening(familyId: String) {
        stopListening()

        // Транзакции
        val txnListener = firestore.collection("families/$familyId/transactions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.forEach { doc ->
                    val txn = doc.toObject(TransactionEntity::class.java)
                    if (txn != null) kotlinx.coroutines.runBlocking { transactionDao.insert(txn) }
                }
            }
        listeners.add(txnListener)

        // Категории
        val catListener = firestore.collection("families/$familyId/categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.forEach { doc ->
                    val cat = doc.toObject(CategoryEntity::class.java)
                    if (cat != null) kotlinx.coroutines.runBlocking { categoryDao.insert(cat) }
                }
            }
        listeners.add(catListener)

        // Задачи
        val taskListener = firestore.collection("families/$familyId/tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.forEach { doc ->
                    val task = doc.toObject(TaskEntity::class.java)
                    if (task != null) kotlinx.coroutines.runBlocking { taskDao.insert(task) }
                }
            }
        listeners.add(taskListener)

        // Цели
        val goalListener = firestore.collection("families/$familyId/goals")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.forEach { doc ->
                    val goal = doc.toObject(GoalEntity::class.java)
                    if (goal != null) kotlinx.coroutines.runBlocking { goalDao.insertGoal(goal) }
                }
            }
        listeners.add(goalListener)

        // Покупки
        val shoppingListener = firestore.collection("families/$familyId/shopping")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.forEach { doc ->
                    val item = doc.toObject(ShoppingItemEntity::class.java)
                    if (item != null) kotlinx.coroutines.runBlocking { shoppingDao.insert(item) }
                }
            }
        listeners.add(shoppingListener)

        Log.d(TAG, "Слушаем семью: $familyId")
    }

    fun stopListening() {
        listeners.forEach { it.remove() }
        listeners.clear()
    }

    // Методы для отправки данных в облако
    suspend fun syncTransaction(familyId: String, txn: TransactionEntity) {
        firestore.collection("families/$familyId/transactions").document(txn.cloudId).set(txn).await()
    }

    suspend fun syncCategory(familyId: String, cat: CategoryEntity) {
        firestore.collection("families/$familyId/categories").document(cat.cloudId).set(cat).await()
    }

    suspend fun syncTask(familyId: String, task: TaskEntity) {
        firestore.collection("families/$familyId/tasks").document(task.cloudId).set(task).await()
    }

    suspend fun syncGoal(familyId: String, goal: GoalEntity) {
        firestore.collection("families/$familyId/goals").document(goal.cloudId).set(goal).await()
    }

    suspend fun syncShoppingItem(familyId: String, item: ShoppingItemEntity) {
        firestore.collection("families/$familyId/shopping").document(item.cloudId).set(item).await()
    }
}
