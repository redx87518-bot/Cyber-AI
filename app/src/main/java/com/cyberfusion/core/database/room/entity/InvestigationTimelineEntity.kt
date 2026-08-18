package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "investigation_timeline",
    foreignKeys = [
        ForeignKey(
            entity = InvestigationEntity::class,
            parentColumns = ["id"],
            childColumns = ["investigationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("investigationId")]
)
data class InvestigationTimelineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val investigationId: Long,
    val event: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: String? = null
)