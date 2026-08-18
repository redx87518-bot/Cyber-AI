package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.ApiCredentialEntity
import com.cyberfusion.core.database.room.entity.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE key = :key LIMIT 1")
    suspend fun getSetting(key: String): SettingEntity?

    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<SettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingEntity)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteSetting(key: String)

    @Query("SELECT * FROM api_credentials")
    fun getAllCredentials(): Flow<List<ApiCredentialEntity>>

    @Query("SELECT * FROM api_credentials WHERE provider = :provider LIMIT 1")
    suspend fun getCredential(provider: String): ApiCredentialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: ApiCredentialEntity)

    @Query("UPDATE api_credentials SET status = :status, updatedAt = :updatedAt WHERE provider = :provider")
    suspend fun updateCredentialStatus(provider: String, status: String, updatedAt: Long)
}