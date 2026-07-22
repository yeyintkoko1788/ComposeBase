package com.yeyint.composebase.network.exception

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.jvm.Throws

class NetworkExceptionInterceptor : Interceptor {

    @Throws()
    override fun intercept(chain : Interceptor.Chain) : Response {

        val response = chain.proceed(chain.request())

        return when (response.isSuccessful) {
            true -> response
            false -> {
                val peeked = response.peekBody(Long.MAX_VALUE)
                Log.e("NetworkError", "HTTP ${response.code}: ${peeked.string()}")
                throw NetworkException(response.body, response.code)
            }
        }
    }
}