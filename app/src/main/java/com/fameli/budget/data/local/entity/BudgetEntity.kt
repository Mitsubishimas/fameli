package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String = "",
    val categoryId: Long? = null,
    val limitAmount: Double = 0.0,
    val month: String = "",
    val alertThreshold: Float = 0.8f,
    val isDeleted: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
) {
    constructor() : this(id = 0, cloudId = "", limitAmount = 0.0, month = "")
}
