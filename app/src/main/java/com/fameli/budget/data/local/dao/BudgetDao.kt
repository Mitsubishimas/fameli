package com.fameli.budget.data.local.dao

import androidx.room.*
import com.fameli.budget.data.local.entity.BudgetEntity
import com.fameli.budget.data.model.BudgetProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE isDeleted = 0 AND month = :month")
    fun getForMonth(month: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long
}
