package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val cloudId: String = "",
    val categoryId: Long,
    val amount: Double,
    val currency: String = "RUB",
    val date: Long,
    val note: String? = null,
    val isDeleted: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
)
