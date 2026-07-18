package com.fameli.budget.data.repository

import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilySyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: FirebaseAuthRepository,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao
) {
    private var listeners: MutableList<ListenerRegistration> = mutableListOf()

    fun startListening(familyId: String) {
        stopListening()

        // Слушаем транзакции
        val txnListener = firestore.collection("families/$familyId/transactions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.forEach { doc ->
                    val txn = doc.toObject(TransactionEntity::class.java) ?: return@forEach
                    kotlinx.coroutines.runBlocking {
                        transactionDao.insert(txn)
                    }
                }
            }
        listeners.add(txnListener)

        // Слушаем категории
        val catListener = firestore.collection("families/$familyId/categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.forEach { doc ->
                    val cat = doc.toObject(CategoryEntity::class.java) ?: return@forEach
                    kotlinx.coroutines.runBlocking {
                        categoryDao.insert(cat)
                    }
                }
            }
        listeners.add(catListener)

        // Слушаем задачи
        val taskListener = firestore.collection("families/$familyId/tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.forEach { doc ->
                    val task = doc.toObject(TaskEntity::class.java) ?: return@forEach
                    kotlinx.coroutines.runBlocking {
                        taskDao.insert(task)
                    }
                }
            }
        listeners.add(taskListener)
    }

    fun stopListening() {
        listeners.forEach { it.remove() }
        listeners.clear()
    }

    suspend fun syncTransactionToCloud(familyId: String, transaction: TransactionEntity) {
        firestore.collection("families/$familyId/transactions")
            .document(transaction.cloudId.ifBlank { java.util.UUID.randomUUID().toString() })
            .set(transaction)
            .await()
    }

    suspend fun syncCategoryToCloud(familyId: String, category: CategoryEntity) {
        firestore.collection("families/$familyId/categories")
            .document(category.cloudId.ifBlank { java.util.UUID.randomUUID().toString() })
            .set(category)
            .await()
    }

    suspend fun syncTaskToCloud(familyId: String, task: TaskEntity) {
        firestore.collection("families/$familyId/tasks")
            .document(task.cloudId.ifBlank { java.util.UUID.randomUUID().toString() })
            .set(task)
            .await()
    }

    suspend fun createFamily(familyName: String): String {
        val userId = authRepository.getUserId() ?: throw Exception("Не авторизован")
        val familyRef = firestore.collection("families").document()
        familyRef.set(mapOf(
            "name" to familyName,
            "createdBy" to userId,
            "createdAt" to System.currentTimeMillis(),
            "members" to listOf(userId)
        )).await()
        return familyRef.id
    }

    suspend fun joinFamily(familyId: String): Boolean {
        val userId = authRepository.getUserId() ?: return false
        val doc = firestore.collection("families").document(familyId).get().await()
        if (!doc.exists()) return false
        
        val members = doc.get("members") as? List<*> ?: emptyList<Any>()
        if (!members.contains(userId)) {
            firestore.collection("families").document(familyId)
                .update("members", members + userId)
                .await()
        }
        return true
    }

    suspend fun getMyFamilies(): List<String> {
        val userId = authRepository.getUserId() ?: return emptyList()
        val snapshot = firestore.collection("families")
            .whereArrayContains("members", userId)
            .get()
            .await()
        return snapshot.documents.map { it.id }
    }
}
