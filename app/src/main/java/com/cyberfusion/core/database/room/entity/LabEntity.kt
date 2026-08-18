package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labs")
data class LabEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val questions: String,
    val hints: String,
    val scenario: String,
    val evidence: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)