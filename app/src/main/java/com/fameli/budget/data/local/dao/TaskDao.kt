package com.fameli.budget.data.local.dao

import androidx.room.*
import com.fameli.budget.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY date ASC, time ASC")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date >= :startOfDay AND date < :endOfDay AND isDeleted = 0 ORDER BY time ASC")
    fun getForDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE cloudId = :cloudId LIMIT 1")
    suspend fun getByCloudId(cloudId: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleComplete(id: Long, completed: Boolean)

    @Query("UPDATE tasks SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)
}
