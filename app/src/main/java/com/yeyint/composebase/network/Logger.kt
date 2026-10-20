package com.yeyint.composebase.network

import android.util.Log
import com.yeyint.composebase.BuildConfig

val SWALLOWED_EXCEPTION = "SWALLOWED_EXCEPTIONS"
var SHOW_SWALLOWED_LOGS = true

private const val DEFAULT_TAG = "APP_TAG"

/**
 * Every helper in this file is a no-op outside debug builds, so nothing logged here — HTTP error
 * bodies, parsed server errors, anything a caller interpolates — reaches Logcat in a release build.
 * (This used to be a "comment this class out before releasing" TODO that nobody could be expected
 * to remember.)
 *
 * Arguments are still evaluated by the caller before the gate runs. If building the message is
 * expensive or reads a stream (e.g. `ResponseBody.string()`), guard the call site with
 * `if (BuildConfig.DEBUG)` as `NetworkExceptionInterceptor` does.
 *
 * Use these instead of `android.util.Log` directly, or the gate is bypassed.
 */
private val loggingEnabled: Boolean get() = BuildConfig.DEBUG

fun showLogD(text : String) {
    if (loggingEnabled) Log.d(DEFAULT_TAG , text)
}

fun showLogI(text : String) {
    if (loggingEnabled) Log.i(DEFAULT_TAG , text)
}

fun showLogV(text : String) {
    if (loggingEnabled) Log.v(DEFAULT_TAG , text)
}

fun showLogW(text : String) {
    if (loggingEnabled) Log.w(DEFAULT_TAG , text)
}

fun showLogD(customTag : String , text : String) {
    if (loggingEnabled) Log.d(customTag , text)
}

fun showLogI(customTag : String , text : String) {
    if (loggingEnabled) Log.i(customTag , text)
}

fun showLogV(customTag : String , text : String) {
    if (loggingEnabled) Log.v(customTag , text)
}

fun showLogW(customTag : String , text : String) {
    if (loggingEnabled) Log.w(customTag , text)
}

/**
 * The one required call for a failure that is deliberately not propagated — a `catch`, or a
 * `runCatching { … }.getOrNull()/getOrDefault()`. Swallowing is sometimes right (a push-token
 * failure must not block sign-in), but a swallowed failure with no log is indistinguishable from
 * "it worked and returned the default", which is how a broken release-only push registration went
 * unnoticed. Filter Logcat on [SWALLOWED_EXCEPTION] to see everything the app has ignored.
 */
fun logSwallowed(text : String , throwable : Throwable? = null) {
    if (SHOW_SWALLOWED_LOGS) {
        if (throwable != null)
            showLogE(text , throwable , SWALLOWED_EXCEPTION)
        else
            showLogE(SWALLOWED_EXCEPTION , text)
    }
}

fun <T> Class<T>.showLogE(text : String) {
    if (loggingEnabled) Log.e(this.name , text)
}

fun showLogE(text : String) {
    if (loggingEnabled) Log.e(DEFAULT_TAG , text)
}

fun showLogE(customTag : String , text : String) {
    if (loggingEnabled) Log.e(customTag , text)
}

fun showLogE(text : String? = null , throwable : Throwable , tag : String? = null) {
    if (!loggingEnabled) return
    // The overload taking a Throwable prints its stack trace itself. (This previously logged
    // `throwable.printStackTrace().toString()` — which returns Unit, so the log line said
    // "kotlin.Unit" while the real trace went to stderr.)
    Log.e(tag ?: DEFAULT_TAG , text ?: "Caused by ${throwable.javaClass.simpleName}" , throwable)
}

fun showLogE(throwable : Throwable) {
    showLogE(null , throwable , null)
}
