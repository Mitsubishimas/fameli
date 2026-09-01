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
        log("Отправка локальных данных в облако...")
        try {
            shoppingDao.getAll().first().forEach { item ->
                val json = JSONObject().apply {
                    put("cloud_id", item.cloudId)
                    put("family_id", fid)
                    put("name", item.name)
                    put("is_purchased", item.isPurchased)
                    put("purchased_by_name", item.purchasedByName)
                    put("created_by_name", item.createdByName)
                    put("created_at", item.createdAt)
                }
                ApiClient.saveShopping(json)
            }
            taskDao.getAll().first().forEach { task ->
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
            }
            transactionDao.getAll().first().forEach { txn ->
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
            log("Отправка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка отправки: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Загрузка данных из облака...")
        try {
            // Покупки — с обновлением статуса
            val shop = ApiClient.getShopping(fid)
            log("Покупок из облака: ${shop.length()}")
            for (i in 0 until shop.length()) {
                val obj = shop.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                val item = ShoppingItemEntity(
                    cloudId = cloudId,
                    name = obj.optString("name", ""),
                    isPurchased = obj.optInt("is_purchased", 0) == 1,
                    purchasedByName = obj.optString("purchased_by_name", ""),
                    createdByName = obj.optString("created_by_name", ""),
                    createdAt = obj.optLong("created_at", System.currentTimeMillis())
                )
                val existing = shoppingDao.getByCloudId(cloudId)
                if (existing == null) {
                    shoppingDao.insert(item)
                    log("Добавлена покупка: ${item.name}")
                } else {
                    shoppingDao.update(item.copy(id = existing.id))
                    log("Обновлена покупка: ${item.name} (куплено: ${item.isPurchased})")
                }
            }

            // Задачи — с обновлением статуса
            val tasks = ApiClient.getTasks(fid)
            log("Задач из облака: ${tasks.length()}")
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
                    log("Добавлена задача: ${task.title}")
                } else {
                    taskDao.update(task.copy(id = existing.id))
                    log("Обновлена задача: ${task.title} (выполнено: ${task.isCompleted})")
                }
            }

            // Транзакции
            val txns = ApiClient.getTransactions(fid)
            log("Транзакций из облака: ${txns.length()}")
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
                if (transactionDao.getByCloudId(txn.cloudId) == null) {
                    transactionDao.insert(txn)
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
