package com.yeyint.composebase.domain.exception

import com.yeyint.composebase.domain.model.FlowReturnResult
import com.yeyint.composebase.ui.exception.ErrorResponse

interface GenericErrorMessageFactory {
    fun getErrorMessage(throwable : Throwable) : CharSequence

    fun getErrorMessage(throwable : Throwable , defaultText : Int) : CharSequence

    fun <T> getSpecificType(throwable : Throwable , defaultText : Int) : FlowReturnResult<T>

    fun <T> getError(throwable : Throwable , defaultText : Int) : FlowReturnResult<T>

    fun <T> getLoginError(throwable: Throwable, defaultText: Int): FlowReturnResult<T>

    fun getErrorCodeAndMessage(throwable: Throwable) : String

    fun getErrorResponse(throwable: Throwable) : ErrorResponse
}