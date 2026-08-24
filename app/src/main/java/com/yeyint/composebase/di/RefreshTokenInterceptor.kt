package com.yeyint.composebase.di

import com.yeyint.composebase.network.components.SecurePreference
import com.yeyint.composebase.network.showLogI
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

/**
 * Attaches the *refresh* token, and is only installed on the dedicated refresh-token OkHttp client
 * so it never collides with [AuthenticationInterceptor]'s access-token header.
 */
class RefreshTokenInterceptor(private val securePreference : SecurePreference) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain : Interceptor.Chain) : Response {
        val refreshToken : String = securePreference.refreshToken
        val builder = chain.request().newBuilder()

        builder.addHeader("Accept" , "application/json")

        // Tokens are never logged.
        if (refreshToken.isNotEmpty())
            builder.addHeader("Authorization" , "Bearer $refreshToken")

        val request = builder.build()
        val response = chain.proceed(request)
        showLogI("AUTH_INSPECTOR", "2nd ${response.code}")
        return response
    }

}
