package com.yeyint.composebase.network.exception

import com.yeyint.composebase.BuildConfig
import com.yeyint.composebase.network.showLogE
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
                // Guarded here, not just inside showLogE: peekBody(...).string() copies the whole
                // error body into memory, and arguments are evaluated before the logger's own gate.
                if (BuildConfig.DEBUG) {
                    showLogE("NetworkError", "HTTP ${response.code}: ${response.peekBody(Long.MAX_VALUE).string()}")
                }
                throw NetworkException(response.body, response.code)
            }
        }
    }
}