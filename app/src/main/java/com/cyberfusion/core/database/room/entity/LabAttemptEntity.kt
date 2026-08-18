package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lab_attempts")
data class LabAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val labId: Long,
    val answers: String,
    val score: Int = 0,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)