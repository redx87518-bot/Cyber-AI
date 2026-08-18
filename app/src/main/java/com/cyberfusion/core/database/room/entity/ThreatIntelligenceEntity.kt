package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "threat_intelligence",
    foreignKeys = [
        ForeignKey(
            entity = IocEntity::class,
            parentColumns = ["id"],
            childColumns = ["iocId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("iocId")]
)
data class ThreatIntelligenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val iocId: Long,
    val provider: String,
    val reputation: String? = null,
    val confidence: Int? = null,
    val severity: String? = null,
    val tags: String? = null,
    val malwareFamily: String? = null,
    val firstSeen: Long? = null,
    val lastSeen: Long? = null,
    val rawResponse: String,
    val createdAt: Long = System.currentTimeMillis()
)