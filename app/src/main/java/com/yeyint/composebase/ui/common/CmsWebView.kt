package com.yeyint.composebase.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

/**
 * Renders CMS-style rich text via a plain platform [WebView] — same recipe as OneCheck's:
 * [WebView.loadDataWithBaseURL] with a responsive CSS wrapper, placed inside an unbounded-height
 * scrolling `Column` so the WebView measures to its content's natural height instead of
 * collapsing to zero (the trick only works when the parent doesn't constrain height — a
 * `LazyColumn` item or fixed-height container would break it). Shared by every screen that shows
 * server-authored HTML (notification details, Terms & Conditions, Privacy Policy).
 *
 * The actual [WebView.loadDataWithBaseURL] call is deferred behind a short delay (same as
 * OneCheck) rather than fired from `factory` — `loadDataWithBaseURL` kicks off HTML parsing/layout
 * immediately, and doing that while the screen's own push/pop transition is still animating is
 * what causes the visible stutter. Letting the transition settle first keeps it smooth.
 */
@Composable
fun CmsWebView(html: String, modifier: Modifier = Modifier, centered: Boolean = false) {
    var isReady by remember { mutableStateOf(false) }
    val cmsBaseUrl = "https://${LocalContext.current.packageName}"
    LaunchedEffect(html) {
        isReady = false
        delay(300)
        isReady = true
    }
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    builtInZoomControls = false
                    setSupportZoom(false)
                    displayZoomControls = false
                    useWideViewPort = true
                    loadWithOverviewMode = true
                }
                isScrollContainer = false
                overScrollMode = WebView.OVER_SCROLL_NEVER
                // The WebView is sized to its full content height inside the parent's own scroll
                // container, so it never scrolls itself — its bars would only flash while the
                // content loads/lays out.
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                // The player is a third-party iframe and needs its own cookies; without this it can
                // load and still refuse to play.
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                // Makes the player's fullscreen button work — see fullscreenChromeClient.
                webChromeClient = fullscreenChromeClient(ctx)
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString() ?: return false
                        // Subframe navigations belong to an embedded player. Handing those to an
                        // external app breaks playback.
                        if (request.isForMainFrame != true) return false
                        openLinkExternally(ctx, url)
                        return true
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        request?.takeIf { it.isForMainFrame }?.let {
                            if ((view?.copyBackForwardList()?.currentIndex ?: 0) > 0) {
                                view?.goBack()
                            } else {
                                // Nothing to go back to: don't strand the rider on the WebView's
                                // error page, hand the address to whoever can open it.
                                openLinkExternally(ctx, it.url.toString())
                            }
                        }
                    }
                }
            }
        },
        update = { webView ->
            if (isReady) {
                webView.loadDataWithBaseURL(
                    cmsBaseUrl,
                    wrapResponsiveHtml(prepareCmsHtml(html), centered),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        // Compose never destroys a WebView on its own: without this an embedded video keeps
        // playing its audio after the rider backs out, and a fullscreen player's view would stay
        // attached to the Activity.
        onRelease = { webView ->
            webView.webChromeClient?.onHideCustomView()
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.destroy()
        },
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Opens a tapped CMS link outside the WebView. Each scheme goes to the app that handles it best —
 * dialer, mail, SMS, Maps, WhatsApp, Telegram, Play Store — and anything else (plain http/https
 * included) to the browser. A package-targeted intent is only a preference: if that app isn't
 * installed, the link falls back to the generic ACTION_VIEW instead of silently doing nothing, which
 * is what a bare `setPackage` does on a phone without it. `intent:` links are parsed as the page
 * wrote them, but refused if they'd target a component rather than a public activity.
 */
private fun openLinkExternally(context: Context, url: String) {
    val uri = Uri.parse(url)
    fun view(pkg: String? = null) = Intent(Intent.ACTION_VIEW, uri).apply {
        pkg?.let(::setPackage)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val candidates: List<Intent?> = when {
        url.startsWith("tel:") -> listOf(Intent(Intent.ACTION_DIAL, uri))
        url.startsWith("mailto:") -> listOf(Intent(Intent.ACTION_SENDTO, uri))
        url.startsWith("sms:") || url.startsWith("ftp:") -> listOf(view())
        url.startsWith("geo:") -> listOf(view("com.google.android.apps.maps"), view())
        url.startsWith("https://wa.me/") || url.contains("whatsapp://") ->
            listOf(view("com.whatsapp"), view())
        url.startsWith("market:") || url.contains("play.google.com") ->
            listOf(view("com.android.vending"), view())
        url.startsWith("intent:") -> listOf(
            runCatching { Intent.parseUri(url, Intent.URI_INTENT_SCHEME) }.getOrNull()?.apply {
                // A link in server content shouldn't be able to name an arbitrary component of
                // this or another app, nor a selector intent.
                component = null
                selector = null
                addCategory(Intent.CATEGORY_BROWSABLE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        url.contains("t.me") -> listOf(view("org.telegram.messenger"), view())
        else -> listOf(view())
    }
    // First one that something can handle; a missing app or an unresolvable link just moves on.
    candidates.filterNotNull().firstOrNull { intent ->
        runCatching { context.startActivity(intent) }.isSuccess
    }
}

/**
 * Cleans CMS HTML before it reaches the WebView. Client-side on purpose — the published content is
 * what it is.
 */
private fun prepareCmsHtml(raw: String): String = raw
    // The editor's own delete-video control ships inside the published body; unremoved it renders
    // as a live trash button on top of the video.
    .replace(Regex("""<button class="video-delete-btn"[\s\S]*?</button>"""), "")
    // A sandboxed iframe without allow-popups can't hand off to the YouTube app, and the attribute
    // varies per CMS version — dropping it is simpler than reasoning about each combination.
    .replace(Regex("""\s*sandbox="[^"]*""""), "")
    // Guards against a restrictive default: the player needs the origin to reach YouTube.
    .replace("<iframe ", """<iframe referrerpolicy="strict-origin-when-cross-origin" """)
    // The CMS writes a fixed height (e.g. 700) on iframes; this makes them a normal video shape.
    .plus("""<style>iframe { width: 100% !important; aspect-ratio: 16/9 !important; height: auto !important; }</style>""")

private fun wrapResponsiveHtml(body: String, centered: Boolean): String = """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
      <style>
        * { box-sizing: border-box; }
        body { margin: 0; padding: 0; overflow-x: hidden; word-wrap: break-word;
               font-family: sans-serif; font-size: 15px; line-height: 1.5; color: #171717;
               ${if (centered) "text-align: center;" else ""} }
        p { margin-top: 0; margin-bottom: 0.75em; }
        h1, h2, h3 { margin-top: 1.2em; margin-bottom: 0.5em; }
        img { max-width: 100% !important; height: auto !important; border-radius: 8px; }
        ul, ol { padding-left: 1.5em; margin-top: 0.5em; margin-bottom: 0.5em; }
      </style>
    </head>
    <body>${body.replace("&nbsp;", " ")}</body>
    </html>
""".trimIndent()
