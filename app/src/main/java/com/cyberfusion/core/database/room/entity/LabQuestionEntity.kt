package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lab_questions",
    foreignKeys = [
        ForeignKey(
            entity = LabEntity::class,
            parentColumns = ["id"],
            childColumns = ["labId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("labId")]
)
data class LabQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val labId: Long,
    val question: String,
    val options: String,
    val correctAnswer: String,
    val explanation: String
)