package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val severity: String,
    val source: String,
    val ioc: String? = null,
    val iocType: String? = null,
    val status: String,
    val evidence: String? = null,
    val aiAnalysis: String? = null,
    val threatIntelSummary: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)