package com.yeyint.composebase.di

import com.yeyint.composebase.network.components.MyPreference
import com.yeyint.composebase.network.showLogI
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

class RefreshTokenInterceptor(private val pref : MyPreference) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain : Interceptor.Chain) : Response {
        val bearerToken : String? = pref.load(MyPreference.KEYS.REFRESH_TOKEN)
        val builder = chain.request().newBuilder()

        builder.addHeader("Accept" , "application/json")
        showLogI("Bearer ${bearerToken}")

        if (bearerToken != null && bearerToken != "")
            builder.addHeader("Authorization" , "Bearer $bearerToken")

        val request = builder.build()
        val response = chain.proceed(request)
        showLogI("AUTH_INSPECTOR", "2nd ${response.code}")
        return response
    }

}
