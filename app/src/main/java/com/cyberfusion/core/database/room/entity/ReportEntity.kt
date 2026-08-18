package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String,
    val content: String,
    val format: String,
    val filePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)