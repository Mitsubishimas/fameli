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
        try {
            // Получаем облачные ID
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
            // Удаляем локальные которых нет в облаке
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
            // Отправляем только НОВЫЕ транзакции
            val cloudTxns = ApiClient.getTransactions(fid)
            val cloudTxnIds = mutableSetOf<String>()
            for (i in 0 until cloudTxns.length()) cloudTxnIds.add(cloudTxns.getJSONObject(i).optString("cloud_id"))
            
            val localTxns = transactionDao.getAll().first().filter { !it.isDeleted && it.cloudId.isNotEmpty() }
            val newTxns = localTxns.filter { !cloudTxnIds.contains(it.cloudId) }
            log("Новых транзакций для отправки: ${newTxns.size}")
            newTxns.forEach { txn ->
                ApiClient.saveTransaction(JSONObject().apply {
                    put("cloud_id", txn.cloudId); put("family_id", fid)
                    put("type", txn.type.uppercase()); put("amount", txn.amount)
                    put("category_name", txn.categoryName); put("note", txn.note)
                    put("date", txn.date); put("last_modified", txn.lastModified)
                })
            }

            // Покупки — только новые
            val cloudShop = ApiClient.getShopping(fid)
            val cloudShopIds = mutableSetOf<String>()
            for (i in 0 until cloudShop.length()) cloudShopIds.add(cloudShop.getJSONObject(i).optString("cloud_id"))
            
            val localShop = shoppingDao.getAll().first().filter { !it.isDeleted && it.cloudId.isNotEmpty() }
            val newShop = localShop.filter { !cloudShopIds.contains(it.cloudId) }
            log("Новых покупок: ${newShop.size}")
            newShop.forEach { item ->
                ApiClient.saveShopping(JSONObject().apply {
                    put("cloud_id", item.cloudId); put("family_id", fid); put("name", item.name)
                    put("is_purchased", item.isPurchased); put("purchased_by_name", item.purchasedByName)
                    put("created_by_name", item.createdByName); put("created_at", item.createdAt)
                    put("last_modified", item.lastModified)
                })
            }

            // Задачи — только новые
            val cloudTasks = ApiClient.getTasks(fid)
            val cloudTaskIds = mutableSetOf<String>()
            for (i in 0 until cloudTasks.length()) cloudTaskIds.add(cloudTasks.getJSONObject(i).optString("cloud_id"))
            
            val localTasks = taskDao.getAll().first().filter { !it.isDeleted && it.cloudId.isNotEmpty() }
            val newTasks = localTasks.filter { !cloudTaskIds.contains(it.cloudId) }
            log("Новых задач: ${newTasks.size}")
            newTasks.forEach { task ->
                ApiClient.saveTask(JSONObject().apply {
                    put("cloud_id", task.cloudId); put("family_id", fid)
                    put("title", task.title); put("description", task.description)
                    put("date", task.date); put("time", task.time)
                    put("is_completed", task.isCompleted); put("created_by_name", task.createdBy)
                    put("last_modified", task.lastModified)
                })
            }

            log("Отправка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка отправки: ${e.message}")
            Result.failure(e)
        }
    }
}
