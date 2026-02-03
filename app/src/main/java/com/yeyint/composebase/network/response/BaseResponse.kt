package com.yeyint.composebase.network.response

abstract class BaseResponse<T> {
    abstract val meta : MetaData
    abstract val body: BodyData<T>
}

data class BodyData<T>(
    val data: T?
)

data class MetaData(
     val statusCode: Int?,
     val success: Boolean,
     val message: String
)
