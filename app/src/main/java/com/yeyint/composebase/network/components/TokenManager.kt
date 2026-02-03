package com.yeyint.composebase.network.components

import android.util.Log
import com.yeyint.composebase.network.service.AuthenticationService
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val authService: AuthenticationService,
    private val prefs: MyPreference
) {

    fun saveToken(token: String) {
        prefs.save(MyPreference.KEYS.ACCESS_TOKEN, token)
        Log.d("token.use", token)
        //prefs.save(MyPreference.KEYS.REFRESH_TOKEN, refreshToken)
    }

    fun refreshTokenSync(): String? {
        return try {
            val response = authService.refreshTokenApiCall().execute()
            if (response.isSuccessful && response.body() != null) {
                val newToken: String = response.body()?.body?.data?.accessToken ?: ""
                saveToken(newToken)
                newToken
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
