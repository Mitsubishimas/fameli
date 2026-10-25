package com.fameli.budget.data.local.dao

import androidx.room.*
import com.fameli.budget.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE isDeleted = 0 AND month = :month ORDER BY categoryName ASC")
    fun getForMonth(month: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE isDeleted = 0 AND month = :month ORDER BY categoryName ASC")
    suspend fun getForMonthOnce(month: String): List<BudgetEntity>

    /** Все бюджеты, включая удалённые — для полной синхронизации */
    @Query("SELECT * FROM budgets")
    suspend fun getAllIncludingDeleted(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE isDeleted = 0")
    suspend fun getAllActive(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE cloudId = :cloudId LIMIT 1")
    suspend fun getByCloudId(cloudId: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE isDeleted = 0 AND month = :month AND categoryName = :categoryName LIMIT 1")
    suspend fun getByMonthAndCategory(month: String, categoryName: String): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Query("UPDATE budgets SET isDeleted = 1, lastModified = :now WHERE id = :id")
    suspend fun softDelete(id: Long, now: Long = System.currentTimeMillis())

    @Query("UPDATE budgets SET limitAmount = :amount, lastModified = :now WHERE id = :id")
    suspend fun updateLimit(id: Long, amount: Double, now: Long = System.currentTimeMillis())
}
