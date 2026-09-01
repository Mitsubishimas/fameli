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
        Result.success(Unit)
    }

    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Загрузка из облака...")
        try {
            // Транзакции — с логированием
            val txns = ApiClient.getTransactions(fid)
            log("Получено транзакций: ${txns.length()}")
            for (i in 0 until txns.length()) {
                val obj = txns.getJSONObject(i)
                val txn = TransactionEntity(
                    cloudId = obj.optString("cloud_id"),
                    type = if (obj.optString("type") == "income") "INCOME" else "EXPENSE",
                    amount = obj.optDouble("amount", 0.0),
                    categoryName = obj.optString("category_name", ""),
                    note = obj.optString("note", ""),
                    date = obj.optLong("date", 0),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = transactionDao.getByCloudId(txn.cloudId)
                if (existing == null) {
                    transactionDao.insert(txn)
                    log("+ Транзакция: ${txn.note.ifBlank { txn.categoryName }} = ${txn.amount}")
                } else {
                    transactionDao.update(txn.copy(localId = existing.localId))
                    log("~ Транзакция: ${txn.note.ifBlank { txn.categoryName }} = ${txn.amount}")
                }
            }

            // Покупки
            val shop = ApiClient.getShopping(fid)
            log("Получено покупок: ${shop.length()}")
            for (i in 0 until shop.length()) {
                val obj = shop.getJSONObject(i)
                val item = ShoppingItemEntity(
                    cloudId = obj.optString("cloud_id"),
                    name = obj.optString("name", ""),
                    isPurchased = obj.optInt("is_purchased", 0) == 1,
                    purchasedByName = obj.optString("purchased_by_name", ""),
                    createdByName = obj.optString("created_by_name", ""),
                    createdAt = obj.optLong("created_at", System.currentTimeMillis()),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = shoppingDao.getByCloudId(item.cloudId)
                if (existing == null) {
                    shoppingDao.insert(item)
                    log("+ Покупка: ${item.name}")
                } else {
                    shoppingDao.update(item.copy(id = existing.id))
                    log("~ Покупка: ${item.name} → ${item.isPurchased}")
                }
            }

            // Задачи
            val tasks = ApiClient.getTasks(fid)
            log("Получено задач: ${tasks.length()}")
            for (i in 0 until tasks.length()) {
                val obj = tasks.getJSONObject(i)
                val task = TaskEntity(
                    cloudId = obj.optString("cloud_id"),
                    title = obj.optString("title", ""),
                    description = obj.optString("description", ""),
                    date = obj.optLong("date", 0),
                    time = obj.optString("time", "12:00"),
                    isCompleted = obj.optInt("is_completed", 0) == 1,
                    createdBy = obj.optString("created_by_name", ""),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = taskDao.getByCloudId(task.cloudId)
                if (existing == null) {
                    taskDao.insert(task)
                    log("+ Задача: ${task.title}")
                } else {
                    taskDao.update(task.copy(id = existing.id))
                    log("~ Задача: ${task.title} → ${task.isCompleted}")
                }
            }

            log("Загрузка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка загрузки: ${e.message}")
            Result.failure(e)
        }
    }
}
