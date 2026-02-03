package com.yeyint.composebase.network

import android.util.Log
import com.yeyint.composebase.BuildConfig

val SWALLOWED_EXCEPTION = "SWALLOWED_EXCEPTIONS"
var SHOW_SWALLOWED_LOGS = true

//TODO comment this class when about to release
fun showLogD(text : String) {
    Log.d("APP_TAG" , text)
}

fun showLogI(text : String) {
    Log.i("APP_TAG" , text)
}

fun showLogV(text : String) {
    Log.v("APP_TAG" , text)
}

fun showLogW(text : String) {
    Log.v("APP_TAG" , text)
}


fun showLogD(customTag : String , text : String) {
    Log.d(customTag , text)
}


fun showLogI(customTag : String , text : String) {
    Log.i(customTag , text)
}

fun showLogV(customTag : String , text : String) {
    Log.v(customTag , text)
}

fun showLogW(customTag : String , text : String) {
    Log.v(customTag , text)
}

fun logSwallowed(text : String , throwable : Throwable? = null) {
    if (SHOW_SWALLOWED_LOGS) {
        if (throwable != null)
            showLogE(text , throwable , SWALLOWED_EXCEPTION)
        else
            showLogE(SWALLOWED_EXCEPTION , text)
    }
}

fun <T> Class<T>.showLogE(text : String) {
    Log.e(this.name , text)
}

fun showLogE(text : String) {
    Log.e("APP_TAG" , text)
}

fun showLogE(customTag : String , text : String) {
    Log.e(customTag , text)
}

fun showLogE(text : String? = null , throwable : Throwable , tag : String? = null) {
    if (text != null)
        Log.e(
            tag ?: "APP_TAG" ,
            "$text"

        )
    val stringBuilder = StringBuilder()
    val stackTrace = throwable.stackTrace
    if (BuildConfig.DEBUG) {
        stringBuilder.append("Caused by ${throwable.javaClass.simpleName}\n")
        stackTrace.forEach {
            stringBuilder.append("           At ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})\n")
        }
        Log.e(
            tag ?: "APP_TAG" ,
            "$stringBuilder"

        )
    }
    Log.e(tag ?: "APP_TAG" , throwable.printStackTrace().toString())

}


fun showLogE(throwable : Throwable) {
    showLogE(null , throwable , null)
}