package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_credentials")
data class ApiCredentialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val provider: String,
    val apiKey: String,
    val model: String = "",
    val isEnabled: Boolean = true,
    val isPrimary: Boolean = false,
    val isFallback: Boolean = false,
    val status: String = "unknown",
    val updatedAt: Long = System.currentTimeMillis()
)
 
