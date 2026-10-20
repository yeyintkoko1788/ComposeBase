package com.yeyint.composebase.ui.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient

/**
 * A [WebChromeClient] that actually implements fullscreen video.
 *
 * `allowfullscreen` in CMS markup is only a request — it does nothing unless the host handles
 * [WebChromeClient.onShowCustomView], which is why an embedded player's fullscreen button is dead
 * with a bare client. The player's view is added to the Activity's decor view and removed again on
 * exit, the standard arrangement, which needs no change to the Compose tree.
 */
fun fullscreenChromeClient(context: Context): WebChromeClient = object : WebChromeClient() {

    private var customView: View? = null
    private var callback: CustomViewCallback? = null

    private val decorView: ViewGroup?
        get() = context.findActivity()?.window?.decorView as? ViewGroup

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (customView != null) {
            // Already fullscreen. Accepting a second view would leak the first.
            callback?.onCustomViewHidden()
            return
        }
        customView = view
        this.callback = callback
        view?.let {
            decorView?.addView(
                it,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    override fun onHideCustomView() {
        customView?.let { decorView?.removeView(it) }
        customView = null
        callback?.onCustomViewHidden()
        callback = null
    }
}
