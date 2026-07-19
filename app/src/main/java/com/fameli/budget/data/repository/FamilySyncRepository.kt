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
    private val taskDao: TaskDao
) {
    companion object {
        private const val TAG = "FAMILY_SYNC"
    }

    private var listeners: MutableList<ListenerRegistration> = mutableListOf()

    suspend fun createFamily(familyName: String): Result<String> {
        return try {
            val userId = authRepository.getUserId() ?: throw Exception("Не авторизован. Войдите в аккаунт.")
            
            // Проверяем что Firestore доступен
            firestore.collection("families").limit(1).get().await()
            
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
            Log.e(TAG, "Ошибка создания семьи: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun joinFamily(familyId: String): Result<Boolean> {
        return try {
            val userId = authRepository.getUserId() ?: throw Exception("Не авторизован")
            
            val doc = firestore.collection("families").document(familyId).get().await()
            if (!doc.exists()) throw Exception("Семья с таким ID не найдена")
            
            val members = (doc.get("members") as? List<*>)?.toMutableList() ?: mutableListOf()
            if (!members.contains(userId)) {
                members.add(userId)
                firestore.collection("families").document(familyId)
                    .update("members", members)
                    .await()
            }
            Log.d(TAG, "Присоединился к семье: $familyId")
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
            Log.e(TAG, "Ошибка получения семей: ${e.message}", e)
            emptyList()
        }
    }

    fun startListening(familyId: String) {
        stopListening()
        try {
            val txnListener = firestore.collection("families/$familyId/transactions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    snapshot?.documents?.forEach { doc ->
                        val txn = doc.toObject(TransactionEntity::class.java)
                        if (txn != null) {
                            kotlinx.coroutines.runBlocking { transactionDao.insert(txn) }
                        }
                    }
                }
            listeners.add(txnListener)
            Log.d(TAG, "Слушаем семью: $familyId")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка слушателя: ${e.message}")
        }
    }

    fun stopListening() {
        listeners.forEach { it.remove() }
        listeners.clear()
    }
}
