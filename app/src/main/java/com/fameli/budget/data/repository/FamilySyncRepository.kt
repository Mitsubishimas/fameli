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

    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Загрузка из облака...")
        try {
            // КАТЕГОРИИ — загружаем ВСЕ
            val cats = ApiClient.getCategories(fid)
            log("Категорий из облака: ${cats.length()}")
            for (i in 0 until cats.length()) {
                val obj = cats.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                if (cloudId.isBlank()) continue // пропускаем с пустым ID
                val typeRaw = obj.optString("type", "EXPENSE").uppercase()
                val cat = CategoryEntity(
                    cloudId = cloudId,
                    name = obj.optString("name", ""),
                    type = if (typeRaw == "INCOME") CategoryType.INCOME else CategoryType.EXPENSE,
                    icon = if (obj.optString("icon", "").isBlank() || obj.optString("icon") == "????") "💰" else obj.optString("icon"),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = categoryDao.getByCloudId(cloudId)
                if (existing == null) {
                    categoryDao.insert(cat)
                    log("+ Категория: ${cat.name}")
                } else {
                    categoryDao.update(cat.copy(id = existing.id))
                }
            }

            // Транзакции
            val cloudTxns = ApiClient.getTransactions(fid)
            val cloudTxnIds = mutableSetOf<String>()
            for (i in 0 until cloudTxns.length()) {
                val obj = cloudTxns.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                cloudTxnIds.add(cloudId)
                val txn = TransactionEntity(
                    cloudId = cloudId,
                    type = if (obj.optString("type").uppercase() == "INCOME") "INCOME" else "EXPENSE",
                    amount = obj.optDouble("amount", 0.0),
                    categoryName = obj.optString("category_name", ""),
                    note = obj.optString("note", ""),
                    date = obj.optLong("date", 0),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis()),
                    isDeleted = obj.optInt("is_deleted", 0) == 1
                )
                val existing = transactionDao.getByCloudId(cloudId)
                if (existing == null) {
                    if (!txn.isDeleted) transactionDao.insert(txn)
                } else {
                    transactionDao.update(txn.copy(localId = existing.localId))
                }
            }
            transactionDao.getAll().first().forEach { local ->
                if (!cloudTxnIds.contains(local.cloudId)) transactionDao.softDelete(local.localId)
            }

            // Покупки
            val cloudShop = ApiClient.getShopping(fid)
            val shopIds = mutableSetOf<String>()
            for (i in 0 until cloudShop.length()) {
                val obj = cloudShop.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                shopIds.add(cloudId)
                val item = ShoppingItemEntity(
                    cloudId = cloudId,
                    name = obj.optString("name", ""),
                    isPurchased = obj.optInt("is_purchased", 0) == 1,
                    purchasedByName = obj.optString("purchased_by_name", ""),
                    createdByName = obj.optString("created_by_name", ""),
                    createdAt = obj.optLong("created_at", System.currentTimeMillis()),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis()),
                    isDeleted = obj.optInt("is_deleted", 0) == 1
                )
                val existing = shoppingDao.getByCloudId(cloudId)
                if (existing == null) {
                    if (!item.isDeleted) shoppingDao.insert(item)
                } else {
                    shoppingDao.update(item.copy(id = existing.id))
                }
            }
            shoppingDao.getAll().first().forEach { local ->
                if (!shopIds.contains(local.cloudId)) shoppingDao.softDelete(local.id)
            }

            // Задачи
            val cloudTasks = ApiClient.getTasks(fid)
            val taskIds = mutableSetOf<String>()
            for (i in 0 until cloudTasks.length()) {
                val obj = cloudTasks.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                taskIds.add(cloudId)
                val task = TaskEntity(
                    cloudId = cloudId,
                    title = obj.optString("title", ""),
                    description = obj.optString("description", ""),
                    date = obj.optLong("date", 0),
                    time = obj.optString("time", "12:00"),
                    isCompleted = obj.optInt("is_completed", 0) == 1,
                    createdBy = obj.optString("created_by_name", ""),
                    repeatType = obj.optString("repeat_type", "NONE"),
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis()),
                    isDeleted = obj.optInt("is_deleted", 0) == 1
                )
                val existing = taskDao.getByCloudId(cloudId)
                if (existing == null) {
                    if (!task.isDeleted) taskDao.insert(task)
                } else {
                    taskDao.update(task.copy(id = existing.id))
                }
            }
            taskDao.getAll().first().forEach { local ->
                if (!taskIds.contains(local.cloudId)) taskDao.softDelete(local.id)
            }

            log("Загрузка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка загрузки: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun syncAllLocalToCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext Result.failure(Exception("Нет семьи"))
        try {
            transactionDao.getAll().first().filter { !it.isDeleted && it.cloudId.isNotEmpty() }.forEach { txn ->
                ApiClient.saveTransaction(JSONObject().apply {
                    put("cloud_id", txn.cloudId); put("family_id", fid)
                    put("type", txn.type.uppercase()); put("amount", txn.amount)
                    put("category_name", txn.categoryName); put("note", txn.note)
                    put("date", txn.date); put("last_modified", txn.lastModified)
                })
            }
            shoppingDao.getAll().first().filter { !it.isDeleted && it.cloudId.isNotEmpty() }.forEach { item ->
                ApiClient.saveShopping(JSONObject().apply {
                    put("cloud_id", item.cloudId); put("family_id", fid); put("name", item.name)
                    put("is_purchased", item.isPurchased); put("purchased_by_name", item.purchasedByName)
                    put("created_by_name", item.createdByName); put("created_at", item.createdAt)
                    put("last_modified", item.lastModified)
                })
            }
            taskDao.getAll().first().filter { !it.isDeleted && it.cloudId.isNotEmpty() }.forEach { task ->
                ApiClient.saveTask(JSONObject().apply {
                    put("cloud_id", task.cloudId); put("family_id", fid)
                    put("title", task.title); put("description", task.description)
                    put("date", task.date); put("time", task.time)
                    put("is_completed", task.isCompleted); put("created_by_name", task.createdBy)
                    put("repeat_type", task.repeatType)
                    put("last_modified", task.lastModified)
                })
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
