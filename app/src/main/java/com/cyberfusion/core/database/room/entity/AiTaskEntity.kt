package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_tasks")
data class AiTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val provider: String? = null,
    val model: String? = null,
    val status: String,
    val resultSummary: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)