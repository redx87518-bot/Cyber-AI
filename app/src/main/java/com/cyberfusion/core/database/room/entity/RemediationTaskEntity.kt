package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "remediation_tasks",
    foreignKeys = [
        ForeignKey(
            entity = RiskEntity::class,
            parentColumns = ["id"],
            childColumns = ["riskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("riskId")]
)
data class RemediationTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val status: String,
    val owner: String? = null,
    val riskId: Long,
    val targetDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)