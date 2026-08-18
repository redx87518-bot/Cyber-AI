package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ioc_enrichment",
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
data class IocEnrichmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val iocId: Long,
    val provider: String,
    val rawData: String,
    val normalizedData: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)