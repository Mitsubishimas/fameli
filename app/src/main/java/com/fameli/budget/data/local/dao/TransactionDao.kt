package com.fameli.budget.data.local.dao

import androidx.room.*
import com.fameli.budget.data.local.entity.TransactionEntity
import com.fameli.budget.data.model.CategoryExpense
import com.fameli.budget.data.model.MonthlyBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getBetween(start: Long, end: Long): Flow<List<TransactionEntity>>

    @Query("""
        SELECT c.name as categoryName, c.color as color, c.type as type, SUM(t.amount) as total
        FROM transactions t INNER JOIN categories c ON t.categoryId = c.id
        WHERE t.date BETWEEN :start AND :end AND t.isDeleted = 0 AND c.isDeleted = 0 AND c.type = :type
        GROUP BY t.categoryId ORDER BY total DESC
    """)
    fun getCategorySums(start: Long, end: Long, type: String): Flow<List<CategoryExpense>>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN c.type = 'INCOME' THEN t.amount ELSE 0 END), 0) as totalIncome,
               COALESCE(SUM(CASE WHEN c.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as totalExpense
        FROM transactions t INNER JOIN categories c ON t.categoryId = c.id
        WHERE t.date BETWEEN :start AND :end AND t.isDeleted = 0 AND c.isDeleted = 0
    """)
    fun getMonthlyBalance(start: Long, end: Long): Flow<MonthlyBalance>

    // Проверка по cloudId
    @Query("SELECT * FROM transactions WHERE cloudId = :cloudId LIMIT 1")
    suspend fun getByCloudId(cloudId: String): TransactionEntity?

    // Вставка только если нет
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionEntity): Long

    // Обновление
    @Update
    suspend fun update(transaction: TransactionEntity)

    // Мягкое удаление
    @Query("UPDATE transactions SET isDeleted = 1 WHERE localId = :id")
    suspend fun softDelete(id: Long)
}
