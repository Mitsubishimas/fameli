package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_transactions")
data class GoalTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String = "",
    val goalId: Long = 0,
    val amount: Double = 0.0,
    val comment: String = "",
    val userName: String = "",
    val userUid: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    constructor() : this(id = 0, cloudId = "", goalId = 0, amount = 0.0)
}
