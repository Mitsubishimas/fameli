package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["cloudId"], unique = true)]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String = "",
    val title: String = "",
    val description: String = "",
    val date: Long = 0,
    val time: String = "12:00",
    val isCompleted: Boolean = false,
    val createdBy: String = "",
    val createdByUid: String = "",
    val repeatType: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY
    val isDeleted: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
) {
    constructor() : this(id = 0, cloudId = "", title = "", date = 0)
}
