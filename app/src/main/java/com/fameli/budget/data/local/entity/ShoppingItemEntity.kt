package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_items",
    indices = [Index(value = ["cloudId"], unique = true)]
)
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val isPurchased: Boolean = false,
    val purchasedByUid: String = "",
    val purchasedByName: String = "",
    val purchasedAt: Long = 0,
    val createdByUid: String = "",
    val createdByName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    constructor() : this(id = 0, cloudId = "", name = "")
}
