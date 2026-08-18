package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_task_history")
data class AiTaskHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val provider: String? = null,
    val model: String? = null,
    val toolsUsed: String? = null,
    val resultStatus: String,
    val summarizedResult: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)