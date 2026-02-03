package com.yeyint.composebase.ui.exception

import android.content.Context
import com.yeyint.composebase.R
import com.yeyint.composebase.fromJson
import com.yeyint.composebase.isNotEmptyString
import com.yeyint.composebase.network.exception.NetworkException
import com.yeyint.composebase.network.exception.NetworkExceptionMessageFactory
import com.yeyint.composebase.network.showLogE
import com.google.gson.Gson
import okhttp3.ResponseBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class NetworkExceptionMessageFactoryImpl @Inject constructor(
    private val context: Context
) : NetworkExceptionMessageFactory {

    @Inject
    lateinit var gson: Gson

    override fun getErrorMessage(networkException: NetworkException): CharSequence {
        showLogE("error code in string : ${networkException.errorCode}")
        return when (networkException.errorCode) {
            400 -> parseMessageFromErrorBody(networkException.errorBody)
            401 -> parseMessageFromErrorBody(networkException.errorBody)
            403 -> parseMessageFromErrorBody(networkException.errorBody)
            404 -> context.getString(R.string.error_server_404)
            408 -> parseMessageFromErrorBody(networkException.errorBody)
            409 -> parseMessageFromErrorBody(networkException.errorBody)
            422 -> parseMessageFromErrorBody(networkException.errorBody)
            500 -> context.getString(R.string.error_server_500)
            503 -> parseMessageFromErrorBody(networkException.errorBody)
            else -> context.getString(R.string.error_generic)
        }
    }

    private fun parseMessageFromErrorBody(errorBody: ResponseBody?): CharSequence {
        if (errorBody == null) {
            return context.getString(R.string.error_generic)
        }

        val errorBodyInString =
            try {
                val error: ErrorResponse = gson.fromJson(errorBody.string())
                if (error.message.isNotEmptyString())
                    return error.message
                else
                    return error.error
            } catch (e: Exception) {
                showLogE("Error occurred at parsing error body string ", e)
                try {
                    errorBody.string()
                } catch (e: Exception) {
                    "Error occurred at parsing error body string"
                }
            }

        showLogE("error body in string : $errorBodyInString")


//        try {
//            val errorBodyJson = JSONObject(errorBodyInString)
//            return errorBodyJson.getString("message")
//        } catch (exception: Exception) {
//            this.javaClass.showLogE("Error in parsing error message body ${exception.message}")
//        }

        return errorBodyInString
    }

    override fun getErrorMessage(unknownHostException: UnknownHostException): CharSequence {
        //return context.getString(R.string.error_no_internet)
        return ""
    }

    override fun getErrorMessage(socketTimeoutException: SocketTimeoutException): CharSequence {
        //return context.getString(R.string.error_socket_timeout)
        return ""
    }

    override fun getErrorMessage(connectException: ConnectException): CharSequence {
        //return context.getString(R.string.error_no_internet)
        return ""
    }

    override fun getErrorMessage(ioException: IOException): CharSequence {
        //return context.getString(R.string.error_io)
        return ""
    }


}