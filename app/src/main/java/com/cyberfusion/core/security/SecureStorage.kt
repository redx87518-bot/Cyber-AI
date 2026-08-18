package com.cyberfusion.core.security

import android.content.Context
import com.cyberfusion.core.database.room.entity.ApiCredentialEntity
import com.cyberfusion.core.database.room.entity.SettingEntity
import com.cyberfusion.core.database.room.repository.SettingsRepository

class SecureStorage(private val context: Context, private val settingsRepository: SettingsRepository) {
    private val prefs = KeystoreHelper.getEncryptedPreferences(context)

    suspend fun saveCredential(credential: ApiCredentialEntity) {
        settingsRepository.insertCredential(credential)
    }

    suspend fun getCredential(provider: String): ApiCredentialEntity? {
        return settingsRepository.getCredential(provider)
    }

    suspend fun saveSetting(key: String, value: String) {
        settingsRepository.insertSetting(key, value)
    }

    suspend fun getSetting(key: String): String? {
        return settingsRepository.getSetting(key)?.value
    }
}