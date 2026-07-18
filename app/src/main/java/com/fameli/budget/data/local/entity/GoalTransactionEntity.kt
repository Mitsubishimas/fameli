package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_transactions")
data class GoalTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String = "",
    val goalId: Long,
    val amount: Double, // положительное - пополнение, отрицательное - снятие
    val comment: String,
    val userName: String,
    val userUid: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
