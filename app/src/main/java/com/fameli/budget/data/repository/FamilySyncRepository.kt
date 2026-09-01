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
        try {
            // Категории — отправляем которых нет
            val cloudCats = ApiClient.getCategories(fid)
            val cloudCatIds = mutableSetOf<String>()
            for (i in 0 until cloudCats.length()) cloudCatIds.add(cloudCats.getJSONObject(i).optString("cloud_id"))
            
            categoryDao.getAll().first().forEach { cat ->
                if (!cloudCatIds.contains(cat.cloudId)) {
                    val json = JSONObject().apply {
                        put("cloud_id", cat.cloudId)
                        put("family_id", fid)
                        put("name", cat.name)
                        put("type", if (cat.type == CategoryType.INCOME) "income" else "expense")
                        put("icon", cat.icon)
                        put("last_modified", cat.lastModified)
                    }
                    ApiClient.saveCategory(json)
                }
            }

            // Транзакции
            val cloudTxns = ApiClient.getTransactions(fid)
            val cloudTxnIds = mutableSetOf<String>()
            for (i in 0 until cloudTxns.length()) cloudTxnIds.add(cloudTxns.getJSONObject(i).optString("cloud_id"))
            
            transactionDao.getAll().first().forEach { txn ->
                if (!cloudTxnIds.contains(txn.cloudId)) {
                    val json = JSONObject().apply {
                        put("cloud_id", txn.cloudId)
                        put("family_id", fid)
                        put("type", txn.type.uppercase())
                        put("amount", txn.amount)
                        put("category_name", txn.categoryName)
                        put("note", txn.note)
                        put("date", txn.date)
                        put("last_modified", txn.lastModified)
                    }
                    ApiClient.saveTransaction(json)
                }
            }

            // Покупки
            val cloudShop = ApiClient.getShopping(fid)
            val cloudShopIds = mutableSetOf<String>()
            for (i in 0 until cloudShop.length()) cloudShopIds.add(cloudShop.getJSONObject(i).optString("cloud_id"))
            
            shoppingDao.getAll().first().forEach { item ->
                if (!cloudShopIds.contains(item.cloudId)) {
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
                }
            }

            // Задачи
            val cloudTasks = ApiClient.getTasks(fid)
            val cloudTaskIds = mutableSetOf<String>()
            for (i in 0 until cloudTasks.length()) cloudTaskIds.add(cloudTasks.getJSONObject(i).optString("cloud_id"))
            
            taskDao.getAll().first().forEach { task ->
                if (!cloudTaskIds.contains(task.cloudId)) {
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
            }

            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            // КАТЕГОРИИ — всегда загружаем
            val cats = ApiClient.getCategories(fid)
            for (i in 0 until cats.length()) {
                val obj = cats.getJSONObject(i)
                val cat = CategoryEntity(
                    cloudId = obj.optString("cloud_id"),
                    name = obj.optString("name"),
                    type = if (obj.optString("type").uppercase() == "INCOME") CategoryType.INCOME else CategoryType.EXPENSE,
                    icon = if (obj.optString("icon", "").isBlank()) "💰" else obj.optString("icon"),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = categoryDao.getByCloudId(cat.cloudId)
                if (existing == null) categoryDao.insert(cat)
            }

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
                if (existing == null) transactionDao.insert(txn)
                else transactionDao.update(txn.copy(localId = existing.localId))
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
                if (existing == null) shoppingDao.insert(item)
                else shoppingDao.update(item.copy(id = existing.id))
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
                if (existing == null) taskDao.insert(task)
                else taskDao.update(task.copy(id = existing.id))
            }

            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
