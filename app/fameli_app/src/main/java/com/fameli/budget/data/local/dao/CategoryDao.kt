package com.fameli.budget.data.local.dao

import androidx.room.*
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type = :type AND isDeleted = 0 ORDER BY name ASC")
    fun getByType(type: CategoryType): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE cloudId = :cloudId LIMIT 1")
    suspend fun getByCloudId(cloudId: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Query("UPDATE categories SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)
}
