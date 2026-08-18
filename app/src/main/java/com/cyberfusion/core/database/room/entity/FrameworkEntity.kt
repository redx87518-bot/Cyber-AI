package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "frameworks")
data class FrameworkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val version: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)