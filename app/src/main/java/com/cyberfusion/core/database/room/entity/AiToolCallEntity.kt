package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_tool_calls")
data class AiToolCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val toolName: String,
    val parameters: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)