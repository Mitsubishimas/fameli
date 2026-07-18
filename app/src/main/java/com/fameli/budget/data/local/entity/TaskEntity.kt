package com.fameli.budget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cloudId: String = "",
    val title: String,
    val description: String = "",
    val date: Long,
    val time: String = "", // "14:30"
    val createdBy: String = "", // имя пользователя
    val createdByUid: String = "", // uid пользователя
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
)
