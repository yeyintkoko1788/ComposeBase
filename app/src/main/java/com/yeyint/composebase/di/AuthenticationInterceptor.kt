package com.yeyint.composebase.di

import com.yeyint.composebase.network.components.AuthEventBus
import com.yeyint.composebase.network.components.MyPreference
import com.yeyint.composebase.network.components.TokenManager
import com.yeyint.composebase.network.showLogD
import com.yeyint.composebase.network.showLogI
import com.yeyint.composebase.uitls.LOCALE_EN
import com.yeyint.composebase.uitls.LOCALE_MM
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AuthenticationInterceptor(
    private val tokenManager: TokenManager,
    private val preference: MyPreference,
    private val authEventBus: AuthEventBus
) : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val bearerToken: String = preference.load(MyPreference.KEYS.ACCESS_TOKEN) ?: ""
        val secretHeaderKey : String = preference.load(MyPreference.KEYS.SECRET_HEADER_KEY) ?: ""
        val systemLang = preference.load(MyPreference.KEYS.LOCALE)
        val language = when(systemLang){
            LOCALE_MM -> "my"
            LOCALE_EN -> "en"
            else -> "zh"
        }
        //val builder = chain.request().newBuilder()
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
        builder.addHeader("Accept", "application/json")
        builder.addHeader("Accept-Language", language)
        builder.addHeader("x-location-token", secretHeaderKey)
        showLogI("Bearer $bearerToken")

        showLogI("x-location-token $secretHeaderKey")
        if (bearerToken != null && bearerToken != "")
            builder.addHeader("Authorization", "Bearer $bearerToken")

        val request = builder.build()
        val response = chain.proceed(request)
        if (response.code == 401) {
            // Token expired, refresh it
            synchronized(this) {
                val newToken: String? = tokenManager.refreshTokenSync()
                if (newToken != null) {
                    // Only close after we have a new token and are about to retry
                    response.close()
                    showLogD("AUTH_INSPECTOR", newToken)
                    val newRequest: Request = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    val secondResponse = chain.proceed(newRequest)
                    showLogI("AUTH_INSPECTOR", "retried ${secondResponse.code}")
                    if (secondResponse.code == 401 || secondResponse.code == 403) {
                        authEventBus.emitSessionExpired()
                    }
                    return secondResponse
                } else {
                    // Refresh failed — session is over. Let the caller observe AuthEventBus
                    // and route back to login; return the original response as-is so
                    // interceptors above can still read its body without crashing.
                    authEventBus.emitSessionExpired()
                }
            }
        }
        showLogI("AUTH_INSPECTOR", "1st ${response.code}")
        return response
    }

}
