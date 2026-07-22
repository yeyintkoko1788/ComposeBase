package com.yeyint.composebase.network.components

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject

/**
 * Encrypted key-value storage for sensitive values that don't belong in [MyPreference]'s plain
 * SharedPreferences (PINs, secrets, tokens you don't want readable via `adb backup`/rooted access).
 */
class SecurePreference @Inject constructor(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_compose_base_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun get(key: String, default: String = ""): String {
        return prefs.getString(key, default) ?: default
    }

    fun clear(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
