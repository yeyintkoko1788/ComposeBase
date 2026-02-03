package com.yeyint.composebase.ui.exception

data class ErrorResponse(
    val statusCode: Int = 0,
    val success: Boolean = false,
    val message: String = "",
    val error: String = "",
    val title: String = ""
)