package com.fameli.budget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        TaskEntity::class,
        GoalEntity::class,
        GoalTransactionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class FameliDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun taskDao(): TaskDao
    abstract fun goalDao(): GoalDao
}
