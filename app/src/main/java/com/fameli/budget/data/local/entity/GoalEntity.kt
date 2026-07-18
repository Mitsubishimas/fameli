package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String = "",
    val title: String,
    val description: String = "",
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val deadline: Long? = null,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
)
