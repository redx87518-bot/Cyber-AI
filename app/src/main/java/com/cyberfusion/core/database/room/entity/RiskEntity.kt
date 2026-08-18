package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "risks")
data class RiskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val likelihood: String,
    val impact: String,
    val riskScore: Int,
    val status: String,
    val owner: String? = null,
    val targetDate: Long? = null,
    val frameworkId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)