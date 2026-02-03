package com.yeyint.composebase.network.response

import com.google.gson.annotations.SerializedName


data class RefreshTokenResponse(
    override val meta: MetaData,
    override val body: BodyData<TokenData>
) : BaseResponse<TokenData>()

data class TokenData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("accessTokenExpiredAt")
    val accessTokenExpiredAt: String?,
    @SerializedName("refreshTokenExpiredAt")
    val refreshTokenExpiredAt: String?
)
