package com.cyberfusion.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.KeyStore

object KeystoreHelper {
    private const val KEYSTORE_ALIAS = "cyberfusion_keystore"
    private const val PREFS_FILE = "cyberfusion_secure_prefs"

    fun getOrCreateSecretKey(): java.security.Key {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
            
            val keyGenerator = javax.crypto.KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
        
        return keyStore.getKey(KEYSTORE_ALIAS, null) as java.security.Key
    }

    fun getEncryptedPreferences(context: Context) = EncryptedSharedPreferences.create(
        PREFS_FILE,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}