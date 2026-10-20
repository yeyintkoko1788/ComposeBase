package com.yeyint.composebase.ui.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/** Unwraps a possibly-wrapped [Context] (ContextThemeWrapper, edge-to-edge/insets helpers, etc.)
 *  down to the real [Activity] — `LocalContext.current` is not guaranteed to be the Activity
 *  itself, and a plain `as? Activity` cast just fails silently on a wrapped one. */
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
