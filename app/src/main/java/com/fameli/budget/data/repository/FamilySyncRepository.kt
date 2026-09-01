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

    // ========== СИНХРОНИЗАЦИЯ: СРАВНЕНИЕ ПО last_modified ==========
    suspend fun syncAllLocalToCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Отправка в облако (только новые/изменённые)...")
        try {
            // Покупки
            val cloudShop = ApiClient.getShopping(fid)
            val cloudShopIds = mutableMapOf<String, JSONObject>()
            for (i in 0 until cloudShop.length()) {
                val obj = cloudShop.getJSONObject(i)
                cloudShopIds[obj.optString("cloud_id")] = obj
            }

            shoppingDao.getAll().first().forEach { item ->
                val cloud = cloudShopIds[item.cloudId]
                if (cloud == null || item.lastModified > cloud.optLong("last_modified", 0)) {
                    val json = JSONObject().apply {
                        put("cloud_id", item.cloudId)
                        put("family_id", fid)
                        put("name", item.name)
                        put("is_purchased", item.isPurchased)
                        put("purchased_by_name", item.purchasedByName)
                        put("created_by_name", item.createdByName)
                        put("created_at", item.createdAt)
                        put("last_modified", item.lastModified)
                    }
                    ApiClient.saveShopping(json)
                    log("Отправлена покупка: ${item.name}")
                }
            }

            // Задачи
            val cloudTasks = ApiClient.getTasks(fid)
            val cloudTaskIds = mutableMapOf<String, JSONObject>()
            for (i in 0 until cloudTasks.length()) {
                val obj = cloudTasks.getJSONObject(i)
                cloudTaskIds[obj.optString("cloud_id")] = obj
            }

            taskDao.getAll().first().forEach { task ->
                val cloud = cloudTaskIds[task.cloudId]
                if (cloud == null || task.lastModified > cloud.optLong("last_modified", 0)) {
                    val json = JSONObject().apply {
                        put("cloud_id", task.cloudId)
                        put("family_id", fid)
                        put("title", task.title)
                        put("description", task.description)
                        put("date", task.date)
                        put("time", task.time)
                        put("is_completed", task.isCompleted)
                        put("created_by_name", task.createdBy)
                        put("last_modified", task.lastModified)
                    }
                    ApiClient.saveTask(json)
                    log("Отправлена задача: ${task.title}")
                }
            }

            // Транзакции
            val cloudTxns = ApiClient.getTransactions(fid)
            val cloudTxnIds = mutableMapOf<String, JSONObject>()
            for (i in 0 until cloudTxns.length()) {
                val obj = cloudTxns.getJSONObject(i)
                cloudTxnIds[obj.optString("cloud_id")] = obj
            }

            transactionDao.getAll().first().forEach { txn ->
                val cloud = cloudTxnIds[txn.cloudId]
                if (cloud == null || txn.lastModified > cloud.optLong("last_modified", 0)) {
                    val json = JSONObject().apply {
                        put("cloud_id", txn.cloudId)
                        put("family_id", fid)
                        put("type", if (txn.type == "INCOME") "income" else "expense")
                        put("amount", txn.amount)
                        put("category_name", txn.categoryName)
                        put("note", txn.note)
                        put("date", txn.date)
                        put("last_modified", txn.lastModified)
                    }
                    ApiClient.saveTransaction(json)
                }
            }

            log("Отправка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка отправки: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Загрузка из облака...")
        try {
            // Покупки
            val shop = ApiClient.getShopping(fid)
            for (i in 0 until shop.length()) {
                val obj = shop.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                val item = ShoppingItemEntity(
                    cloudId = cloudId,
                    name = obj.optString("name", ""),
                    isPurchased = obj.optInt("is_purchased", 0) == 1,
                    purchasedByName = obj.optString("purchased_by_name", ""),
                    createdByName = obj.optString("created_by_name", ""),
                    createdAt = obj.optLong("created_at", System.currentTimeMillis()),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = shoppingDao.getByCloudId(cloudId)
                if (existing == null) {
                    shoppingDao.insert(item)
                    log("+ Покупка: ${item.name}")
                } else if (item.lastModified > existing.lastModified) {
                    shoppingDao.update(item.copy(id = existing.id))
                    log("~ Покупка: ${item.name} (куплено: ${item.isPurchased})")
                }
            }

            // Задачи
            val tasks = ApiClient.getTasks(fid)
            for (i in 0 until tasks.length()) {
                val obj = tasks.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                val task = TaskEntity(
                    cloudId = cloudId,
                    title = obj.optString("title", ""),
                    description = obj.optString("description", ""),
                    date = obj.optLong("date", 0),
                    time = obj.optString("time", "12:00"),
                    isCompleted = obj.optInt("is_completed", 0) == 1,
                    createdBy = obj.optString("created_by_name", ""),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = taskDao.getByCloudId(cloudId)
                if (existing == null) {
                    taskDao.insert(task)
                    log("+ Задача: ${task.title}")
                } else if (task.lastModified > existing.lastModified) {
                    taskDao.update(task.copy(id = existing.id))
                    log("~ Задача: ${task.title} (выполнено: ${task.isCompleted})")
                }
            }

            // Транзакции
            val txns = ApiClient.getTransactions(fid)
            for (i in 0 until txns.length()) {
                val obj = txns.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                val txn = TransactionEntity(
                    cloudId = cloudId,
                    type = if (obj.optString("type") == "income") "INCOME" else "EXPENSE",
                    amount = obj.optDouble("amount", 0.0),
                    categoryName = obj.optString("category_name", ""),
                    note = obj.optString("note", ""),
                    date = obj.optLong("date", 0),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = transactionDao.getByCloudId(cloudId)
                if (existing == null) {
                    transactionDao.insert(txn)
                    log("+ Транзакция: ${txn.note}")
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
