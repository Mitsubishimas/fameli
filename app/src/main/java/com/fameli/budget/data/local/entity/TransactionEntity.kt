package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["cloudId"], unique = true)]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val cloudId: String = "",
    val type: String = "EXPENSE", // INCOME или EXPENSE
    val amount: Double = 0.0,
    val categoryId: Long = 0,
    val categoryName: String = "",
    val note: String = "",
    val date: Long = 0,
    val isDeleted: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
) {
    constructor() : this(localId = 0, cloudId = "", type = "EXPENSE", amount = 0.0, categoryId = 0, date = 0)
}
