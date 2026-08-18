package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.SettingsDao
import com.cyberfusion.core.database.room.entity.ApiCredentialEntity
import com.cyberfusion.core.database.room.entity.SettingEntity
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsDao: SettingsDao) {
    val allSettings: Flow<List<SettingEntity>> = settingsDao.getAllSettings()

    suspend fun getSetting(key: String): SettingEntity? = settingsDao.getSetting(key)
    suspend fun insertSetting(key: String, value: String) {
        val existing = settingsDao.getSetting(key)
        if (existing != null) {
            settingsDao.insertSetting(existing.copy(value = value, updatedAt = System.currentTimeMillis()))
        } else {
            settingsDao.insertSetting(SettingEntity(key = key, value = value))
        }
    }
    suspend fun deleteSetting(key: String) = settingsDao.deleteSetting(key)

    val allCredentials: Flow<List<ApiCredentialEntity>> = settingsDao.getAllCredentials()
    suspend fun getCredential(provider: String): ApiCredentialEntity? = settingsDao.getCredential(provider)
    suspend fun insertCredential(credential: ApiCredentialEntity) = settingsDao.insertCredential(credential)
    suspend fun updateCredentialStatus(provider: String, status: String) =
        settingsDao.updateCredentialStatus(provider, status, System.currentTimeMillis())
}