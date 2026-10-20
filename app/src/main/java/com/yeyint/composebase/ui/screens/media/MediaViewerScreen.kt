package com.yeyint.composebase.ui.screens.media

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.common.HeaderIconButton
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.mediumBodySmall
import com.yeyint.composebase.ui.theme.semiboldBodyLarge
import com.yeyint.composebase.ui.theme.surface_brand
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.text_inverted

/**
 * Full-screen, paged, pinch-zoomable image viewer — ported from Easy Choice Merchant's own
 * `MediaViewerScreen`. That app also pages videos (chat attachments can be either); nothing in
 * this app ever attaches a video, so the video half of that screen isn't ported — this is images
 * only. If that ever changes, port `VideoPage`/`MediaItemVO.isVideo` across the same way
 * `ZoomableImage` already was.
 *
 * No ViewModel: [imageUrls]/[title] travel straight in the nav key (see `NavRoute.MediaViewer`)
 * since the set being viewed — and what to call it — is a UI selection the caller already knows,
 * not a query anything else would ask.
 */
@Composable
fun MediaViewerScreen(
    title: String,
    imageUrls: List<String>,
    startIndex: Int,
    onBack: () -> Unit
) {
    if (imageUrls.isEmpty()) {
        // Defensive: nothing routes here with an empty list, but an empty pager throws rather
        // than rendering nothing, so the guard is worth more than the branch costs.
        onBack()
        return
    }

    val pagerState = rememberPagerState(
        initialPage = startIndex.coerceIn(0, imageUrls.lastIndex),
        pageCount = { imageUrls.size }
    )
    // Set by whichever image page is currently pinched. While true the pager stops consuming
    // horizontal drags, so panning a zoomed photo doesn't flip to the next one.
    var isZoomed by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(surface_default)) {
        ViewerHeader(
            title = title,
            position = pagerState.currentPage + 1,
            total = imageUrls.size,
            onBack = onBack
        )
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = !isZoomed,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { page ->
            ZoomableImage(
                url = imageUrls[page],
                // Only the settled page counts as active — see ZoomableImage's own doc.
                isActive = pagerState.currentPage == page,
                onZoomChange = { zoomed ->
                    if (pagerState.currentPage == page) isZoomed = zoomed
                },
                onTap = {}
            )
        }
    }
}

@Composable
private fun ViewerHeader(
    title: String,
    position: Int,
    total: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(surface_brand)
            .statusBarsPadding()
            .height(HEADER_HEIGHT)
            .padding(horizontal = 16.dp)
    ) {
        HeaderIconButton(
            iconRes = R.drawable.ic_back,
            contentDescription = stringResource(R.string.cd_back),
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.semiboldBodyLarge,
            color = text_inverted,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                // Left: 40dp back button + 8dp gap. Right: same only when the counter
                // is visible (total > 1); no right-side element when there's one image.
                .padding(start = 48.dp, end = if (total > 1) 48.dp else 0.dp)
                .basicMarquee()
        )
        // Hidden for a single item — a "1/ 1" counter is noise.
        if (total > 1) {
            Text(
                text = stringResource(R.string.media_counter, position, total),
                style = MaterialTheme.typography.mediumBodySmall,
                color = text_inverted,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

private val HEADER_HEIGHT = 56.dp

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MediaViewerHeaderPreview() {
    BasePreviewWrapper {
        Column(modifier = Modifier.navigationBarsPadding()) {
            ViewerHeader(title = "Failed Delivery Proof", position = 1, total = 24, onBack = {})
        }
    }
}
