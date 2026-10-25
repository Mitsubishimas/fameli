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
    private val budgetDao: BudgetDao,
    private val familyManager: FamilyManager
) {
    private fun log(msg: String) = AppLogger.log("SYNC", msg)

    // ================= ЗАГРУЗКА =================
    suspend fun syncAllFromCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId
            ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Загрузка из облака...")
        try {
            // 1. Категории
            val cats = ApiClient.getCategories(fid)
            log("Категорий из облака: ${cats.length()}")
            for (i in 0 until cats.length()) {
                val obj = cats.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                if (cloudId.isBlank()) continue
                val typeRaw = obj.optString("type", "EXPENSE").uppercase()
                val cat = CategoryEntity(
                    cloudId = cloudId,
                    name = obj.optString("name", ""),
                    type = if (typeRaw == "INCOME") CategoryType.INCOME else CategoryType.EXPENSE,
                    icon = obj.optString("icon", "").let {
                        if (it.isBlank() || it == "????" || it == "?") "💰" else it
                    },
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = categoryDao.getByCloudId(cloudId)
                if (existing == null) categoryDao.insert(cat)
                else if (cat.lastModified >= existing.lastModified) categoryDao.update(cat.copy(id = existing.id))
            }

            // 2. Транзакции
            val cloudTxns = ApiClient.getTransactions(fid)
            log("Получено транзакций: ${cloudTxns.length()}")
            val cloudTxnIds = mutableSetOf<String>()
            for (i in 0 until cloudTxns.length()) {
                val obj = cloudTxns.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                if (cloudId.isBlank()) continue
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
                } else if (txn.lastModified >= existing.lastModified) {
                    transactionDao.update(txn.copy(localId = existing.localId))
                }
            }
            // удаляем локальные, которых нет в облаке (только если ответ не пустой)
            if (cloudTxns.length() > 0) {
                transactionDao.getAll().first().forEach { local ->
                    if (local.cloudId.isNotEmpty() && !cloudTxnIds.contains(local.cloudId)) {
                        transactionDao.softDelete(local.localId)
                    }
                }
            } else {
                log("Транзакции: пустой ответ API — локальные не трогаем")
            }

            // 3. Покупки
            val cloudShop = ApiClient.getShopping(fid)
            log("Получено покупок: ${cloudShop.length()}")
            val shopIds = mutableSetOf<String>()
            for (i in 0 until cloudShop.length()) {
                val obj = cloudShop.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                if (cloudId.isBlank()) continue
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
                } else if (item.lastModified >= existing.lastModified) {
                    shoppingDao.update(item.copy(id = existing.id))
                }
            }
            if (cloudShop.length() > 0) {
                shoppingDao.getAll().first().forEach { local ->
                    if (local.cloudId.isNotEmpty() && !shopIds.contains(local.cloudId)) {
                        shoppingDao.softDelete(local.id)
                    }
                }
            } else {
                log("Покупки: пустой ответ API — локальные не трогаем")
            }

            // 4. Задачи
            val cloudTasks = ApiClient.getTasks(fid)
            log("Получено задач: ${cloudTasks.length()}")
            val taskIds = mutableSetOf<String>()
            for (i in 0 until cloudTasks.length()) {
                val obj = cloudTasks.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                if (cloudId.isBlank()) continue
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
                } else if (task.lastModified >= existing.lastModified) {
                    taskDao.update(task.copy(id = existing.id))
                }
            }
            if (cloudTasks.length() > 0) {
                taskDao.getAll().first().forEach { local ->
                    if (local.cloudId.isNotEmpty() && !taskIds.contains(local.cloudId)) {
                        taskDao.softDelete(local.id)
                    }
                }
            } else {
                log("Задачи: пустой ответ API — локальные не трогаем")
            }

            // 5. Бюджеты
            val cloudBudgets = ApiClient.getBudgets(fid)
            log("Бюджетов из облака: ${cloudBudgets.length()}")
            val budgetIds = mutableSetOf<String>()
            for (i in 0 until cloudBudgets.length()) {
                val obj = cloudBudgets.getJSONObject(i)
                val cloudId = obj.optString("cloud_id")
                if (cloudId.isBlank()) continue
                budgetIds.add(cloudId)
                val remote = BudgetEntity(
                    cloudId = cloudId,
                    categoryId = if (obj.has("category_id") && !obj.isNull("category_id"))
                        obj.optLong("category_id") else null,
                    categoryName = obj.optString("category_name", ""),
                    limitAmount = obj.optDouble("limit_amount", 0.0),
                    month = obj.optString("month", ""),
                    alertThreshold = obj.optDouble("alert_threshold", 0.8).toFloat(),
                    isDeleted = obj.optInt("is_deleted", 0) == 1,
                    lastModified = obj.optLong("last_modified", System.currentTimeMillis())
                )
                val existing = budgetDao.getByCloudId(cloudId)
                if (existing == null) {
                    if (!remote.isDeleted && remote.categoryName.isNotBlank()) budgetDao.insert(remote)
                } else if (remote.lastModified >= existing.lastModified) {
                    budgetDao.update(remote.copy(id = existing.id))
                }
            }
            if (cloudBudgets.length() > 0) {
                budgetDao.getAllActive().forEach { local ->
                    if (local.cloudId.isNotEmpty() && !budgetIds.contains(local.cloudId)) {
                        budgetDao.softDelete(local.id)
                    }
                }
            }

            log("Загрузка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка загрузки: ${e.message}")
            Result.failure(e)
        }
    }

    // ================= ОТПРАВКА =================
    suspend fun syncAllLocalToCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId
            ?: return@withContext Result.failure(Exception("Нет семьи"))
        log("Отправка в облако...")
        try {
            // Транзакции
            transactionDao.getAll().first()
                .filter { !it.isDeleted && it.cloudId.isNotEmpty() }
                .forEach { txn ->
                    val ok = ApiClient.saveTransaction(JSONObject().apply {
                        put("cloud_id", txn.cloudId); put("family_id", fid)
                        put("type", txn.type.uppercase()); put("amount", txn.amount)
                        put("category_name", txn.categoryName); put("note", txn.note)
                        put("date", txn.date); put("last_modified", txn.lastModified)
                    })
                    if (!ok) log("↑ отказ (outdated): ${txn.note}")
                }

            // Покупки
            shoppingDao.getAll().first()
                .filter { !it.isDeleted && it.cloudId.isNotEmpty() }
                .forEach { item ->
                    ApiClient.saveShopping(JSONObject().apply {
                        put("cloud_id", item.cloudId); put("family_id", fid); put("name", item.name)
                        put("is_purchased", item.isPurchased); put("purchased_by_name", item.purchasedByName)
                        put("created_by_name", item.createdByName); put("created_at", item.createdAt)
                        put("last_modified", item.lastModified)
                    })
                }

            // Задачи
            taskDao.getAll().first()
                .filter { !it.isDeleted && it.cloudId.isNotEmpty() }
                .forEach { task ->
                    ApiClient.saveTask(JSONObject().apply {
                        put("cloud_id", task.cloudId); put("family_id", fid)
                        put("title", task.title); put("description", task.description)
                        put("date", task.date); put("time", task.time)
                        put("is_completed", task.isCompleted); put("created_by_name", task.createdBy)
                        put("repeat_type", task.repeatType)
                        put("last_modified", task.lastModified)
                    })
                }

            // Бюджеты
            budgetDao.getAllIncludingDeleted()
                .filter { it.cloudId.isNotEmpty() }
                .forEach { b ->
                    if (b.isDeleted) {
                        ApiClient.deleteBudget(b.cloudId, b.lastModified)
                    } else {
                        ApiClient.saveBudget(JSONObject().apply {
                            put("cloud_id", b.cloudId); put("family_id", fid)
                            put("category_name", b.categoryName)
                            put("limit_amount", b.limitAmount)
                            put("month", b.month)
                            put("alert_threshold", b.alertThreshold.toDouble())
                            put("last_modified", b.lastModified)
                            if (b.categoryId != null) put("category_id", b.categoryId)
                        })
                    }
                }

            log("Отправка завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            log("Ошибка отправки: ${e.message}")
            Result.failure(e)
        }
    }

    // ================= УДАЛЕНИЯ =================
    suspend fun pushDeleteTransaction(cloudId: String, lastModified: Long) = ApiClient.deleteTransaction(cloudId, lastModified)
    suspend fun pushDeleteShopping(cloudId: String, lastModified: Long) = ApiClient.deleteShopping(cloudId, lastModified)
    suspend fun pushDeleteTask(cloudId: String, lastModified: Long) = ApiClient.deleteTask(cloudId, lastModified)
    suspend fun pushDeleteCategory(cloudId: String, lastModified: Long) = ApiClient.deleteCategory(cloudId, lastModified)
    suspend fun pushDeleteBudget(cloudId: String, lastModified: Long) = ApiClient.deleteBudget(cloudId, lastModified)

    /** Бюджет: локальное изменение → сразу в облако */
    suspend fun pushBudget(b: BudgetEntity): Boolean = withContext(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: return@withContext false
        if (b.cloudId.isBlank()) return@withContext false
        if (b.isDeleted) ApiClient.deleteBudget(b.cloudId, b.lastModified)
        else ApiClient.saveBudget(JSONObject().apply {
            put("cloud_id", b.cloudId); put("family_id", fid)
            put("category_name", b.categoryName)
            put("limit_amount", b.limitAmount)
            put("month", b.month)
            put("alert_threshold", b.alertThreshold.toDouble())
            put("last_modified", b.lastModified)
            if (b.categoryId != null) put("category_id", b.categoryId)
        })
    }
}
