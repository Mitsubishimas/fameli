package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String = "",
    val name: String = "",
    val type: CategoryType = CategoryType.EXPENSE,
    val icon: String = "💰",
    val color: Long = 0xFF1B6B4A,
    val isDefault: Boolean = false,
    val isDeleted: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
) {
    constructor() : this(id = 0, cloudId = "", name = "", type = CategoryType.EXPENSE)
}

enum class CategoryType { INCOME, EXPENSE }
