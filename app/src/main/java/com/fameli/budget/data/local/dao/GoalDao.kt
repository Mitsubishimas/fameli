package com.fameli.budget.data.local.dao

import androidx.room.*
import com.fameli.budget.data.local.entity.GoalEntity
import com.fameli.budget.data.local.entity.GoalTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAll(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getById(id: Long): GoalEntity?

    @Query("SELECT * FROM goals WHERE cloudId = :cloudId LIMIT 1")
    suspend fun getByCloudId(cloudId: String): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("UPDATE goals SET currentAmount = :amount WHERE id = :id")
    suspend fun updateAmount(id: Long, amount: Double)

    @Query("UPDATE goals SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleComplete(id: Long, completed: Boolean)

    @Query("UPDATE goals SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT * FROM goal_transactions WHERE goalId = :goalId AND isDeleted = 0 ORDER BY timestamp ASC")
    fun getTransactions(goalId: Long): Flow<List<GoalTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: GoalTransactionEntity): Long
}
