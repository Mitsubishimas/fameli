package com.fameli.budget.data.repository

import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.data.remote.ApiClient
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

    // ========== ОТПРАВКА В ОБЛАКО ==========
    suspend fun syncAllLocalToCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            // Категории
            categoryDao.getAll().first().forEach { cat ->
                val json = JSONObject().apply {
                    put("cloud_id", cat.cloudId.ifBlank { "cat_${cat.id}" })
                    put("family_id", fid)
                    put("name", cat.name)
                    put("type", if (cat.type == CategoryType.INCOME) "income" else "expense")
                    put("icon", cat.icon)
                    put("last_modified", cat.lastModified)
                }
                ApiClient.saveCategory(json)
            }
            // Транзакции
            transactionDao.getAll().first().forEach { txn ->
                val json = JSONObject().apply {
                    put("cloud_id", txn.cloudId.ifBlank { "txn_${txn.localId}" })
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
            // Покупки
            shoppingDao.getAll().first().forEach { item ->
                val json = JSONObject().apply {
                    put("cloud_id", item.cloudId.ifBlank { "shop_${item.id}" })
                    put("family_id", fid)
                    put("name", item.name)
                    put("is_purchased", item.isPurchased)
                    put("purchased_by_name", item.purchasedByName)
                    put("created_by_name", item.createdByName)
                    put("created_at", item.createdAt)
                }
                ApiClient.saveShopping(json)
            }
            // Задачи
            taskDao.getAll().first().forEach { task ->
                val json = JSONObject().apply {
                    put("cloud_id", task.cloudId.ifBlank { "task_${task.id}" })
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
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // ========== ЗАГРУЗКА ИЗ ОБЛАКА ==========
    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            // Категории
            val cats = ApiClient.getCategories(fid)
            for (i in 0 until cats.length()) {
                val obj = cats.getJSONObject(i)
                val type = if (obj.optString("type") == "income") CategoryType.INCOME else CategoryType.EXPENSE
                val cat = CategoryEntity(
                    cloudId = obj.optString("cloud_id"),
                    name = obj.optString("name"),
                    type = type,
                    icon = obj.optString("icon", "💰"),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                if (categoryDao.getByCloudId(cat.cloudId) == null) categoryDao.insert(cat)
            }

            // Транзакции
            val txns = ApiClient.getTransactions(fid)
            for (i in 0 until txns.length()) {
                val obj = txns.getJSONObject(i)
                val type = if (obj.optString("type") == "income") "INCOME" else "EXPENSE"
                val txn = TransactionEntity(
                    cloudId = obj.optString("cloud_id"),
                    type = type,
                    amount = obj.optDouble("amount", 0.0),
                    categoryName = obj.optString("category_name", ""),
                    note = obj.optString("note", ""),
                    date = obj.optLong("date", 0),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                if (transactionDao.getByCloudId(txn.cloudId) == null) transactionDao.insert(txn)
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
                    createdAt = obj.optLong("created_at", System.currentTimeMillis())
                )
                if (shoppingDao.getByCloudId(item.cloudId) == null) shoppingDao.insert(item)
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
                if (taskDao.getByCloudId(task.cloudId) == null) taskDao.insert(task)
            }

            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
