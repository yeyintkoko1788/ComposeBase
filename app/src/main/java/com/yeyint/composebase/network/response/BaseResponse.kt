package com.yeyint.composebase.network.response

import com.google.gson.annotations.SerializedName

abstract class BaseResponse<T> {
    abstract val meta : MetaData
    abstract val body: BodyData<T>
}

data class BodyData<T>(
    val data: T?
)

data class MetaData(
     // Without this the shared Gson (LOWER_CASE_WITH_UNDERSCORES) looks for "status_code" and this
     // is silently null on every response. Found by ResponseDtoSerializedNameTest.
     @SerializedName("statusCode") val statusCode: Int?,
     val success: Boolean,
     val message: String
)
