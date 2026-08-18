package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lab_progress")
data class LabProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val labId: Long,
    val completed: Boolean = false,
    val score: Int = 0,
    val attempts: Int = 0,
    val lastAttemptAt: Long? = null
)