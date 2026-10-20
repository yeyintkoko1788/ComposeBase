package com.yeyint.composebase.ui.screens.media

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.CircularProgressIndicator
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.yeyint.composebase.ui.theme.surface_brand
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * A pinch- and double-tap-zoomable image. Ported from Easy Choice Merchant's own
 * `ZoomableImage` — same gesture loop, same reasoning.
 *
 * The gesture loop is hand-written because `detectTransformGestures` consumes every pointer
 * change it processes, and a **one-finger drag produces a non-zero pan even at scale 1** (see
 * this project's own CLAUDE.md gotcha). That consumption swallowed the swipe before the pager
 * could read it, so paging did not work at all. The loop below only claims the gesture when it is
 * genuinely this page's — a multi-touch pinch, or a drag on an already-zoomed image — and
 * otherwise leaves the events untouched so they reach the pager's drag handler above this node.
 *
 * [onZoomChange] reports whether this page is currently zoomed, which the pager uses to stop its
 * own scrolling. Both halves are needed: the consumption rule lets a swipe through at rest, and
 * this flag stops a swipe hijacking a pan once zoomed in.
 *
 * @param isActive false when the pager has settled on another page; resets the zoom so returning
 *   to this page shows it fit again, and so no stale zoom state can leave the pager locked.
 */
@Composable
fun ZoomableImage(
    url: String,
    isActive: Boolean,
    onZoomChange: (Boolean) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        // A graphicsLayer's own `clip` only bounds its content to its own *unscaled* local size —
        // Coil already draws entirely within that box, so there was nothing there to clip. The
        // actual overflow happens one step later: scaleX/scaleY are applied when that layer's
        // already-rendered content is composited into its parent, painting larger than the layout
        // box regardless of the child's own clip setting. Only a clip on the container the scaled
        // layer draws into stops that — hence clipToBounds() here, not on the AsyncImage itself.
        modifier = modifier.fillMaxSize().clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        val scope = rememberCoroutineScope()
        // Per page and per URL: on a slow connection the pager would otherwise show a blank page
        // with nothing to say the photo is still on its way.
        var isLoading by remember(url) { mutableStateOf(true) }
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val viewportWidth = constraints.maxWidth.toFloat()
        val viewportHeight = constraints.maxHeight.toFloat()

        // Report zoom state to the pager whenever it crosses the threshold, not on every frame.
        val isZoomed = scale > ZOOM_EPSILON
        LaunchedEffect(isZoomed) { onZoomChange(isZoomed) }

        LaunchedEffect(isActive) {
            if (!isActive) {
                scale = 1f
                offset = Offset.Zero
            }
        }

        /**
         * Keeps the image from being dragged off-screen. At scale 1 there is no slack, so the
         * offset is pinned to zero and a sideways drag falls through to the pager.
         */
        fun clamp(candidate: Offset, forScale: Float): Offset {
            val maxX = (viewportWidth * (forScale - 1f) / 2f).coerceAtLeast(0f)
            val maxY = (viewportHeight * (forScale - 1f) / 2f).coerceAtLeast(0f)
            return Offset(
                x = candidate.x.coerceIn(-maxX, maxX),
                y = candidate.y.coerceIn(-maxY, maxY)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Hand-rolled instead of detectTransformGestures, which consumes *every*
                    // pointer change it sees — including a one-finger drag, whose pan is non-zero
                    // even at scale 1. Consuming that swallowed the swipe before the pager could
                    // read it, so paging never worked.
                    //
                    // The rule here: only take the gesture when it is genuinely ours — a
                    // multi-touch pinch, or a drag on an already-zoomed page. Otherwise leave the
                    // events unconsumed and let them reach the pager's drag handler, which sits
                    // above this node and reads them on the main pass.
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        do {
                            val event = awaitPointerEvent()
                            val pointers = event.changes.count { it.pressed }
                            val isPinch = pointers > 1
                            val isZoomedNow = scale > ZOOM_EPSILON

                            if (isPinch || isZoomedNow) {
                                val zoom = event.calculateZoom()
                                val pan = event.calculatePan()
                                val centroid = event.calculateCentroid(useCurrent = false)

                                // calculateZoom() can return NaN/Infinity for an instant when two
                                // pointers land on (or lift off from) almost exactly the same
                                // point — a near-zero previous/current distance division. Once
                                // `scale` becomes NaN it stays NaN forever: every later
                                // comparison and coerceIn against a NaN silently no-ops instead of
                                // throwing, including `isZoomedNow = scale > ZOOM_EPSILON`, which
                                // is false for NaN — so the image goes blank AND stops responding
                                // to further zoom gestures at the same time. Skipping the
                                // degenerate frame instead of applying it is the actual fix.
                                if (zoom.isFinite() && zoom > 0f) {
                                    val newScale = (scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)

                                    // Zoom about the pinch centroid rather than the middle of the
                                    // screen, so the point under the fingers stays under the fingers.
                                    val centroidFromCentre = Offset(
                                        x = centroid.x - viewportWidth / 2f,
                                        y = centroid.y - viewportHeight / 2f
                                    )
                                    val scaleDelta = newScale / scale
                                    val adjusted = (offset + centroidFromCentre) * scaleDelta -
                                        centroidFromCentre + pan
                                    if (adjusted.x.isFinite() && adjusted.y.isFinite()) {
                                        scale = newScale
                                        offset = clamp(adjusted, newScale)
                                    }
                                }

                                event.changes.forEach { if (it.positionChanged()) it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onTap() },
                        onDoubleTap = { position ->
                            scope.launch {
                                val target = if (scale > ZOOM_EPSILON) 1f else DOUBLE_TAP_SCALE
                                val targetOffset = if (target == 1f) {
                                    Offset.Zero
                                } else {
                                    // Zoom toward the tapped point.
                                    clamp(
                                        Offset(
                                            x = (viewportWidth / 2f - position.x) * (target - 1f),
                                            y = (viewportHeight / 2f - position.y) * (target - 1f)
                                        ),
                                        target
                                    )
                                }
                                animateZoom(
                                    fromScale = scale,
                                    toScale = target,
                                    fromOffset = offset,
                                    toOffset = targetOffset
                                ) { s, o -> scale = s; offset = o }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = url,
                contentDescription = null,
                onState = { state ->
                    // Empty is the state before the request has started, which is still "not
                    // there yet" to the rider; only Success/Error end the wait (an error leaves
                    // the page blank rather than spinning forever).
                    isLoading = state is AsyncImagePainter.State.Loading ||
                        state is AsyncImagePainter.State.Empty
                },
                // Fit, matching the design: the photo is centred with letterboxing above and
                // below rather than cropped to fill.
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )
            // Outside the image's graphicsLayer, so it stays centred and unscaled while zooming.
            if (isLoading) {
                CircularProgressIndicator(color = surface_brand)
            }
        }
    }
}

private suspend fun animateZoom(
    fromScale: Float,
    toScale: Float,
    fromOffset: Offset,
    toOffset: Offset,
    onUpdate: (Float, Offset) -> Unit
) {
    val animatable = Animatable(0f)
    animatable.animateTo(1f) {
        val t = value
        onUpdate(
            fromScale + (toScale - fromScale) * t,
            Offset(
                x = fromOffset.x + (toOffset.x - fromOffset.x) * t,
                y = fromOffset.y + (toOffset.y - fromOffset.y) * t
            )
        )
    }
    // Snap the tail end so a float epsilon can't leave the page permanently "zoomed".
    if (abs(toScale - 1f) < 0.001f) onUpdate(1f, Offset.Zero)
}

/** Above this, the page counts as zoomed: it owns drags and the pager stops scrolling. */
private const val ZOOM_EPSILON = 1.01f

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 5f
private const val DOUBLE_TAP_SCALE = 2.5f
