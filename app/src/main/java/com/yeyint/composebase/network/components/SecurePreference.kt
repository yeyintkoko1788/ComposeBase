package com.yeyint.composebase.network.components

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encrypted key-value storage for credentials that must not sit in [MyPreference]'s plain
 * SharedPreferences, where they are readable via `adb backup` or on a rooted device.
 *
 * Access and refresh tokens live here exclusively — they were deliberately removed from
 * [MyPreference.KEYS] so there is only one place they can be read or written.
 */
@Singleton
class SecurePreference @Inject constructor(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var accessToken: String
        get() = get(KEY_ACCESS_TOKEN)
        set(value) = save(KEY_ACCESS_TOKEN, value)

    var refreshToken: String
        get() = get(KEY_REFRESH_TOKEN)
        set(value) = save(KEY_REFRESH_TOKEN, value)

    /** Value sent as the `x-location-token` header. */
    var secretHeaderKey: String
        get() = get(KEY_SECRET_HEADER_KEY)
        set(value) = save(KEY_SECRET_HEADER_KEY, value)

    fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun get(key: String, default: String = ""): String {
        return prefs.getString(key, default) ?: default
    }

    fun clear(key: String) {
        prefs.edit().remove(key).apply()
    }

    /** Drops every credential. Call on logout and on unrecoverable session expiry. */
    fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_SECRET_HEADER_KEY)
            .apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        /** Also referenced by `res/xml/backup_rules.xml` to keep this file out of cloud backups. */
        const val FILE_NAME = "secure_compose_base_prefs"

        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_SECRET_HEADER_KEY = "secret_header_key"
    }
}
