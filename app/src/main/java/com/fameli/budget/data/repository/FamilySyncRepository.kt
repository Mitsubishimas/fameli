package com.fameli.budget.data.repository

import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.data.remote.ApiClient
import com.fameli.budget.data.remote.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilySyncRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
    private val shoppingDao: ShoppingDao,
    private val familyManager: FamilyManager
) {

    private fun log(msg: String) = AppLogger.log("SYNC", msg)

    suspend fun syncAllLocalToCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Отправка...")
        try {
            // Только локальные транзакции (не удалённые)
            val cloudTxns = ApiClient.getTransactions(fid)
            val cloudIds = mutableSetOf<String>()
            for (i in 0 until cloudTxns.length()) cloudIds.add(cloudTxns.getJSONObject(i).optString("cloud_id"))

            transactionDao.getAll().first().forEach { txn ->
                if (!cloudIds.contains(txn.cloudId)) {
                    val json = JSONObject().apply {
                        put("cloud_id", txn.cloudId)
                        put("family_id", fid)
                        put("type", txn.type.lowercase())
                        put("amount", txn.amount)
                        put("category_name", txn.categoryName)
                        put("note", txn.note)
                        put("date", txn.date)
                        put("last_modified", txn.lastModified)
                    }
                    ApiClient.saveTransaction(json)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Загрузка...")
        try {
            // Получаем облачные ID
            val txns = ApiClient.getTransactions(fid)
            val cloudIds = mutableSetOf<String>()
            
            for (i in 0 until txns.length()) {
                val obj = txns.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                cloudIds.add(cloudId)
                
                val typeRaw = obj.optString("type", "EXPENSE")
                val type = if (typeRaw.uppercase() == "INCOME") "INCOME" else "EXPENSE"
                
                val txn = TransactionEntity(
                    cloudId = cloudId,
                    type = type,
                    amount = obj.optDouble("amount", 0.0),
                    categoryName = obj.optString("category_name", ""),
                    note = obj.optString("note", ""),
                    date = obj.optLong("date", 0),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = transactionDao.getByCloudId(cloudId)
                if (existing == null) transactionDao.insert(txn)
                else transactionDao.update(txn.copy(localId = existing.localId))
            }

            // Удаляем локальные, которых нет в облаке
            val localTxns = transactionDao.getAll().first()
            localTxns.forEach { local ->
                if (!cloudIds.contains(local.cloudId)) {
                    transactionDao.softDelete(local.localId)
                    log("Удалена локально: ${local.note}")
                }
            }

            log("Загрузка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка: ${e.message}")
            Result.failure(e)
        }
    }
}
