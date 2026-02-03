package com.yeyint.composebase.ui.exception

import android.content.Context
import android.util.Log
import com.yeyint.composebase.R
import com.yeyint.composebase.domain.exception.GenericErrorMessageFactory
import com.yeyint.composebase.domain.model.FlowReturnResult
import com.yeyint.composebase.network.exception.NetworkException
import com.yeyint.composebase.network.exception.NetworkExceptionMessageFactory
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


class GenericErrorMessageFactoryImpl @Inject constructor(
    private val context: Context,
    private val networkExceptionMessageFactory: NetworkExceptionMessageFactory
) : GenericErrorMessageFactory {
    override fun getErrorMessage(throwable: Throwable): CharSequence {
        return getErrorMessage(throwable, 0)
    }

    override fun <T> getLoginError(throwable: Throwable, defaultText: Int): FlowReturnResult<T> {
        return when (throwable) {
            is UnknownHostException -> FlowReturnResult.NetworkErrorResult
            is SocketTimeoutException -> FlowReturnResult.NetworkErrorResult
            is ConnectException -> FlowReturnResult.NetworkErrorResult
            is NetworkException -> FlowReturnResult.ErrorResult(getErrorMessage(throwable))
            else -> FlowReturnResult.ErrorResult(getErrorMessage(throwable, defaultText))
        }
    }

    override fun getErrorCodeAndMessage(throwable: Throwable): String {
        return when(throwable) {
            is NetworkException -> "${throwable.errorCode},${networkExceptionMessageFactory.getErrorMessage(throwable)}"
            else -> "0,${getErrorMessage(throwable)}"
        }
    }

    override fun getErrorResponse(throwable: Throwable): ErrorResponse {
        return when(throwable) {
            is NetworkException -> parseErrorResponse(throwable.errorBody?.string())
            is UnknownHostException -> ErrorResponse(
                statusCode = 0,
                success = false,
                message = context.getString(R.string.error_no_internet_connection),
                error = "",
                title = ""
            )
            is SocketTimeoutException -> ErrorResponse(
                statusCode = 0,
                success = false,
                message = context.getString(R.string.error_no_internet_connection),
                error = "",
                title = ""
            )
            is ConnectException -> ErrorResponse(
                statusCode = 0,
                success = false,
                message = context.getString(R.string.error_no_internet_connection),
                error = "",
                title = ""
            )
            is IOException -> ErrorResponse(
                statusCode = 0,
                success = false,
                message = throwable.message
                    ?: context.getString(R.string.error_no_internet_connection),
                error = "",
                title = ""
            )
            else -> ErrorResponse(
                statusCode = 0,
                success = false,
                message = throwable.message ?: context.getString(R.string.error_generic),
                error = "",
                title = ""
            )
        }
    }

    override fun getErrorMessage(throwable: Throwable, defaultText: Int): CharSequence {
        return when (throwable) {
            is UnknownHostException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            is SocketTimeoutException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            is ConnectException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            is NetworkException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            is IOException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            else -> {
                return try {
                    context.getString(defaultText)
                } catch (e: Exception) {
                    throwable.message ?: context.getString(R.string.error_generic)
                }
            }
        }
    }

    override fun <T> getSpecificType(throwable: Throwable, defaultText: Int): FlowReturnResult<T> {
        return when (throwable) {
            is UnknownHostException -> FlowReturnResult.NetworkErrorResult
            is SocketTimeoutException -> FlowReturnResult.NetworkErrorResult
            is ConnectException -> FlowReturnResult.NetworkErrorResult
            is NetworkException -> {
                return when (throwable.errorCode) {
                    401 -> FlowReturnResult.SessionExpired
                    else -> FlowReturnResult.ErrorResult(getErrorMessage(throwable, defaultText))
                }
            }
            is IOException -> FlowReturnResult.NetworkErrorResult
            else -> FlowReturnResult.ErrorResult(getErrorMessage(throwable, defaultText))
        }
    }

    override fun <T> getError(throwable: Throwable, defaultText: Int): FlowReturnResult<T> {

        return when (throwable) {

            //is IOException -> ReturnResult.NetworkErrorResult
            is UnknownHostException -> FlowReturnResult.NetworkErrorResult
            is SocketTimeoutException -> FlowReturnResult.NetworkErrorResult
            is ConnectException -> FlowReturnResult.NetworkErrorResult
            is NetworkException -> getNetworkErrorType(throwable, defaultText)
            else
            -> FlowReturnResult.ErrorResult("Something went wrong! Please try again!")
        }
    }

    private fun <T> getNetworkErrorType(
        networkException: NetworkException,
        defaultText: Int
    ): FlowReturnResult<T> {

//        val errorBody = try {
//            networkException.errorBody?.string()
//        } catch (e: Exception) {
//            Log.e("ErrorBodyRead", "Error reading error body: ${e.message}")
//            null
//        }
        return when (networkException.errorCode) {
            301 -> FlowReturnResult.NewVersion
            401 -> FlowReturnResult.SessionExpired
            //400 -> FlowReturnResult.AlreadyCreate
            //401 -> FlowReturnResult.TempDisable
            402 -> FlowReturnResult.PaymentOverdue
            //404 -> FlowReturnResult.NotFound(parseErrorResponse(errorBody))
            405 -> FlowReturnResult.InvalidOTP(extractValidUntil(networkException.message))
            429 -> FlowReturnResult.TemporarilyBlocked(extractBlockedUntil(networkException.message))
            406 -> FlowReturnResult.UserNotFound
            410 -> FlowReturnResult.UserProfileUnavailable
            417 -> FlowReturnResult.UserProfileLeft
            503 -> FlowReturnResult.ServerMaintenance(errorMsg = getErrorMessage(networkException, defaultText))
            else -> FlowReturnResult.ErrorResult(
                errorMsg = getErrorMessage(networkException, defaultText)
            )
        }
    }

    private fun parseErrorResponse(errorBody: String?): ErrorResponse {
        Log.d("ErrorBody", errorBody ?: "Error body is null")

        return try {
            val jsonObject = JSONObject(errorBody ?: "{}")
            Log.d("ParsedJSONObject", jsonObject.toString())

            val statusCode = jsonObject.optInt("statusCode", 0)
            val success = jsonObject.optBoolean("success", false)
            val message = jsonObject.optString("message", "")
            val error = jsonObject.optString("error", "")
            val title = jsonObject.optString("title", "")

            Log.d(
                "ParsedFields",
                "statusCode: $statusCode, success: $success, message: $message, error: $error, title: $title"
            )

            val errorResponse = ErrorResponse(
                statusCode = statusCode,
                success = success,
                message = message,
                error = error,
                title = title
            )
            Log.d("ParsedError", "ErrorResponse: $errorResponse")

            errorResponse
        } catch (e: JSONException) {
            Log.e("JSONError", "Error parsing error body: ${e.message}")
            ErrorResponse() // Default empty error response
        }
    }

    private fun parse4011ErrorResponse(errorBody: String?): ErrorResponse {
        Log.d("ErrorBody", errorBody ?: "Error body is null")

        return try {
            val jsonObject = JSONObject(errorBody ?: "{}")
            Log.d("ParsedJSONObject", jsonObject.toString())

            val statusCode = jsonObject.optInt("statusCode", 0)
            val success = jsonObject.optBoolean("success", false)
            val message = jsonObject.optString("message", "")
            val error = jsonObject.optString("error", "")
            val title = jsonObject.optString("title", "")

            Log.d(
                "ParsedFields",
                "statusCode: $statusCode, success: $success, message: $message, error: $error, title: $title"
            )

            val errorResponse = ErrorResponse(
                statusCode = statusCode,
                success = success,
                message = message,
                error = error,
                title = title
            )
            Log.d("ParsedError", "ErrorResponse: $errorResponse")

            errorResponse
        } catch (e: JSONException) {
            Log.e("JSONError", "Error parsing error body: ${e.message}")
            ErrorResponse() // Default empty error response
        }
    }


    private fun extractValidUntil(message: String?): String {
        return message?.substringAfter("valid until ")?.substringBefore(".") ?: "After 3 minutes"
    }

    private fun extractBlockedUntil(message: String?): String {
        return message?.substringAfter("blocked until ")?.substringBefore(".") ?: "3 minutes"
    }

}