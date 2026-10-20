package com.yeyint.composebase.network.components

import com.yeyint.composebase.network.logSwallowed
import com.yeyint.composebase.network.service.AuthenticationService
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val authService: AuthenticationService,
    private val securePreference: SecurePreference,
    private val prefs: MyPreference
) {

    fun saveToken(token: String) {
        securePreference.accessToken = token
    }

    fun saveRefreshToken(token: String) {
        securePreference.refreshToken = token
    }

    /** Clears credentials from encrypted storage and the non-sensitive session keys alongside it. */
    fun logout() {
        securePreference.clearTokens()
        prefs.logout()
    }

    fun refreshTokenSync(): String? {
        return try {
            val response = authService.refreshTokenApiCall().execute()
            if (response.isSuccessful && response.body() != null) {
                val newToken: String = response.body()?.body?.data?.accessToken ?: ""
                if (newToken.isEmpty()) {
                    null
                } else {
                    saveToken(newToken)
                    newToken
                }
            } else {
                null
            }
        } catch (e: Exception) {
            // A failed refresh reads downstream as "no token" and ends the session — without this
            // line that is indistinguishable from the user simply being signed out.
            logSwallowed("TokenManager: token call failed, returning null", e)
            null
        }
    }
}
