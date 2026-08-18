package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "iocs")
data class IocEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val value: String,
    val type: String,
    val source: String,
    val reputation: String? = null,
    val confidence: Int? = null,
    val severity: String? = null,
    val firstSeen: Long? = null,
    val lastSeen: Long? = null,
    val tags: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)