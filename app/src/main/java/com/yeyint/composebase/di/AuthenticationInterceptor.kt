package com.yeyint.composebase.di

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
    private val preference: MyPreference
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
            response.close()
            //var secondResponse : Response? = null
            synchronized(this) {
                val newToken: String? = tokenManager.refreshTokenSync()
                if (newToken != null) {
                    // Retry the request with the new token
                    showLogD("AUTH_INSPECTOR", newToken)
                    val newRequest: Request = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    //showLogI("AUTH_INSPECTOR", "2st ${secondResponse!!.code}")
                    val secondResponse = chain.proceed(newRequest)
                    showLogI("AUTH_INSPECTOR", "3rd ${secondResponse.code}")
                    return secondResponse
                }
            }
        }
        showLogI("AUTH_INSPECTOR", "1st ${response.code}")
        return response
    }

}
