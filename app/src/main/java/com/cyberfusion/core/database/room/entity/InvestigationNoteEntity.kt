package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "investigation_notes",
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
data class InvestigationNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val investigationId: Long,
    val content: String,
    val author: String = "analyst",
    val timestamp: Long = System.currentTimeMillis()
)