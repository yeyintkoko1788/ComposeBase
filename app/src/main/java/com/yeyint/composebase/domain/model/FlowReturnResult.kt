package com.yeyint.composebase.domain.model

import com.yeyint.composebase.ui.exception.ErrorResponse


sealed class FlowReturnResult<out T> {
    class ErrorResponseErrorResult<T>(var response : ErrorResponse) : FlowReturnResult<T>()
    class ErrorResult<T>(var errorMsg: Any, var data: T? = null) : FlowReturnResult<T>()
    class ErrorCodeAndMsg<T>(var code: Any, var msg: Any? = null) : FlowReturnResult<T>()
    class PositiveResult<T>(var data: T) : FlowReturnResult<T>()
    class InitialResult<T> : FlowReturnResult<T>()
    class LoadingRelsult<T> : FlowReturnResult<T>()
    class ValidationErrorResult<T>(var msg: Any) : FlowReturnResult<T>()
    object EmptyResult : FlowReturnResult<Nothing>()
    object PaymentOverdue : FlowReturnResult<Nothing>()
    class ServerMaintenance<T>(var errorMsg: Any) : FlowReturnResult<T>()
    object SessionExpired : FlowReturnResult<Nothing>()
    object NewVersion : FlowReturnResult<Nothing>()
    object NetworkErrorResult : FlowReturnResult<Nothing>()
    object SocketTimeOutResult : FlowReturnResult<Nothing>()
    object IOExceptionResult : FlowReturnResult<Nothing>()
    data class NotFound(val errorResponse: ErrorResponse) : FlowReturnResult<Nothing>()
    object TempDisable : FlowReturnResult<Nothing>()
    object AlreadyCreate : FlowReturnResult<Nothing>()
    data class InvalidOTP(val validUntil: String) : FlowReturnResult<Nothing>()
    data class TemporarilyBlocked(val blockedUntil: String) : FlowReturnResult<Nothing>()
    object UserNotFound : FlowReturnResult<Nothing>()
    object UserProfileUnavailable : FlowReturnResult<Nothing>()
    object UserProfileLeft : FlowReturnResult<Nothing>()

    override fun toString(): String {
        return getFlowReturnResultType(this)
    }
}


fun <T> FlowReturnResult<T>.compare(
    other: FlowReturnResult<T>,
    inTestMode: Boolean = false
): Boolean {
    return if (this.javaClass != other.javaClass) false
    else {
        showInLog(this, other, inTestMode)
        when (this) {
            is FlowReturnResult.PositiveResult -> {
                this.data != null && (other as FlowReturnResult.PositiveResult).data != null && (this as FlowReturnResult.PositiveResult).data!! == other.data
            }

            is FlowReturnResult.ErrorResult -> {
                this.data == (other as FlowReturnResult.ErrorResult).data && this.errorMsg == other.errorMsg
            }

            is FlowReturnResult.ValidationErrorResult -> {
                this.msg == (other as FlowReturnResult.ValidationErrorResult).msg
            }

            else -> {
                true
            }
        }
    }
}

private fun <T> showInLog(
    `this`: FlowReturnResult<T>,
    that: FlowReturnResult<T>,
    inTestMode: Boolean
) {
    if (inTestMode) {
        println("this : ${getFlowReturnResultType(`this`)}")
        println("that : ${getFlowReturnResultType(that)}")
    } else {
        //do nothing
    }
}

fun getFlowReturnResultType(returnResult: FlowReturnResult<*>): String {
    return when (returnResult) {
        is FlowReturnResult.PositiveResult -> "PositiveResult(${returnResult.data ?: "no data content"})"
        is FlowReturnResult.ErrorResult -> "ErrorResult(${returnResult.errorMsg},${returnResult.data ?: "no data content"})"
        is FlowReturnResult.LoadingRelsult -> "LoadingResult"
        is FlowReturnResult.EmptyResult -> "EmptyResult"
        is FlowReturnResult.ValidationErrorResult -> "ValidationErrorResult(${returnResult.msg})"
        is FlowReturnResult.NewVersion -> "NewUpdate"
        is FlowReturnResult.PaymentOverdue -> "MembershipExpired"
        is FlowReturnResult.SessionExpired -> "SessionExpired"
        is FlowReturnResult.InvalidOTP -> "InvalidOTP(validUntil=${returnResult.validUntil})"
        is FlowReturnResult.TemporarilyBlocked -> "TemporarilyBlocked(blockedUntil=${returnResult.blockedUntil})"
        else -> "SomethingWentWrong"
    }
}
