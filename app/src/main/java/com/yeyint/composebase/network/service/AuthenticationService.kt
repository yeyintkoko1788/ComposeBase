package com.yeyint.composebase.network.service

import com.yeyint.composebase.network.response.RefreshTokenResponse
import retrofit2.Call
import retrofit2.http.GET

interface AuthenticationService {

    @GET("/customer/api/v1/auth/generate-access-token")
    fun refreshTokenApiCall(
    ): Call<RefreshTokenResponse>
}

