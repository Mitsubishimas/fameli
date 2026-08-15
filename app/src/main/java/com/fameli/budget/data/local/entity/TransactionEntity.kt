package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val cloudId: String = "",
    val categoryId: Long = 0,
    val amount: Double = 0.0,
    val currency: String = "RUB",
    val date: Long = 0,
    val note: String? = null,
    val isDeleted: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
) {
    constructor() : this(localId = 0, cloudId = "", categoryId = 0, amount = 0.0, date = 0)
}
