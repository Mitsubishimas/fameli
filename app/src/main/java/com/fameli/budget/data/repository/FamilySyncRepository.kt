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
        log("Отправка (только изменённые)...")
        try {
            // Транзакции — сравниваем с облаком
            val cloudTxns = ApiClient.getTransactions(fid)
            val cloudTxnMap = mutableMapOf<String, JSONObject>()
            for (i in 0 until cloudTxns.length()) {
                val obj = cloudTxns.getJSONObject(i)
                cloudTxnMap[obj.optString("cloud_id")] = obj
            }
            transactionDao.getAll().first().forEach { txn ->
                val cloud = cloudTxnMap[txn.cloudId]
                val cloudLastMod = cloud?.optLong("last_modified", 0) ?: 0
                if (cloud == null || txn.lastModified > cloudLastMod) {
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
                    log("↑ ${txn.note}")
                }
            }

            // Покупки
            val cloudShop = ApiClient.getShopping(fid)
            val cloudShopMap = mutableMapOf<String, JSONObject>()
            for (i in 0 until cloudShop.length()) {
                val obj = cloudShop.getJSONObject(i)
                cloudShopMap[obj.optString("cloud_id")] = obj
            }
            shoppingDao.getAll().first().forEach { item ->
                val cloud = cloudShopMap[item.cloudId]
                val cloudLastMod = cloud?.optLong("last_modified", 0) ?: 0
                if (cloud == null || item.lastModified > cloudLastMod) {
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
                    log("↑ ${item.name}")
                }
            }

            // Задачи
            val cloudTasks = ApiClient.getTasks(fid)
            val cloudTaskMap = mutableMapOf<String, JSONObject>()
            for (i in 0 until cloudTasks.length()) {
                val obj = cloudTasks.getJSONObject(i)
                cloudTaskMap[obj.optString("cloud_id")] = obj
            }
            taskDao.getAll().first().forEach { task ->
                val cloud = cloudTaskMap[task.cloudId]
                val cloudLastMod = cloud?.optLong("last_modified", 0) ?: 0
                if (cloud == null || task.lastModified > cloudLastMod) {
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
                    log("↑ ${task.title}")
                }
            }

            log("Отправка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Загрузка (только изменённые)...")
        try {
            // Транзакции
            val txns = ApiClient.getTransactions(fid)
            for (i in 0 until txns.length()) {
                val obj = txns.getJSONObject(i)
                val txn = TransactionEntity(
                    cloudId = obj.optString("cloud_id"),
                    type = if (obj.optString("type").uppercase() == "INCOME") "INCOME" else "EXPENSE",
                    amount = obj.optDouble("amount", 0.0),
                    categoryName = obj.optString("category_name", ""),
                    note = obj.optString("note", ""),
                    date = obj.optLong("date", 0),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = transactionDao.getByCloudId(txn.cloudId)
                if (existing == null) {
                    transactionDao.insert(txn)
                    log("+ ${txn.note}")
                } else if (txn.lastModified > existing.lastModified) {
                    transactionDao.update(txn.copy(localId = existing.localId))
                    log("~ ${txn.note}")
                }
            }

            // Покупки
            val shop = ApiClient.getShopping(fid)
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
                    log("+ ${item.name}")
                } else if (item.lastModified > existing.lastModified) {
                    shoppingDao.update(item.copy(id = existing.id))
                    log("~ ${item.name}")
                }
            }

            // Задачи
            val tasks = ApiClient.getTasks(fid)
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
                    log("+ ${task.title}")
                } else if (task.lastModified > existing.lastModified) {
                    taskDao.update(task.copy(id = existing.id))
                    log("~ ${task.title}")
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
