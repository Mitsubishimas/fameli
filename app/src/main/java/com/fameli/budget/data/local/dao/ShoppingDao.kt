package com.fameli.budget.data.local.dao

import androidx.room.*
import com.fameli.budget.data.local.entity.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items WHERE isDeleted = 0 ORDER BY isPurchased ASC, createdAt DESC")
    fun getAll(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE isPurchased = 0 AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getActive(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItemEntity): Long

    @Update
    suspend fun update(item: ShoppingItemEntity)

    @Query("UPDATE shopping_items SET isPurchased = 1, purchasedByUid = :uid, purchasedByName = :userName, purchasedAt = :timestamp WHERE id = :id")
    suspend fun markPurchased(id: Long, uid: String, userName: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE shopping_items SET isPurchased = 0, purchasedByUid = '', purchasedByName = '', purchasedAt = 0 WHERE id = :id")
    suspend fun markUnpurchased(id: Long)

    @Query("UPDATE shopping_items SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)
}
