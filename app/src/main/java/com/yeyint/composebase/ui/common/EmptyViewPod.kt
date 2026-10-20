package com.yeyint.composebase.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.domain.model.ErrorVO
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.boldHeading6
import com.yeyint.composebase.ui.theme.mediumBodyMedium
import com.yeyint.composebase.ui.theme.neutral_text
import com.yeyint.composebase.ui.theme.neutral_text_light

/**
 * Generic "empty state" screen driven by an [ErrorVO] (not-found, no-internet, server error,
 * etc). Callers can either show a retry action, a logout action, or neither.
 *
 * Set [scrollable] when this sits inside a `PullToRefreshBox` (Material 3) — see below.
 */
@Composable
fun EmptyViewPod(
    modifier: Modifier = Modifier,
    error: ErrorVO,
    onRetry: () -> Unit,
    showRetry: Boolean = true,
    onLogOut: (() -> Unit)? = null,
    /**
     * Pull-to-refresh only works on a state that can actually scroll: a pull gesture is delivered
     * to `PullToRefreshBox` as nested scroll, and a plain non-scrollable `Column` never dispatches
     * any, so pulling on an empty or error screen silently does nothing. With this on, the Column
     * becomes a scroll container.
     *
     * The modifier order is the whole trick: `fillMaxSize()` BEFORE `verticalScroll()`, on this
     * same node. `verticalScroll` hands its content unbounded height, and a `fillMaxSize()` applied
     * after it (or inside a scrollable wrapper around this composable) silently does nothing on
     * the unbounded axis — which breaks `Arrangement.Center`, pinning the content to the top.
     * Don't wrap this in your own `Modifier.verticalScroll`; pass `scrollable = true` instead.
     */
    scrollable: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .let { if (scrollable) it.verticalScroll(rememberScrollState()) else it },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty),
            contentDescription = stringResource(R.string.cd_empty),
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = error.title,
            style = MaterialTheme.typography.boldHeading6,
            color = neutral_text,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = error.subtitle,
            style = MaterialTheme.typography.mediumBodyMedium,
            color = neutral_text_light,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        if (onLogOut != null) {
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.txt_logout),
                onClick = onLogOut,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        } else if (showRetry) {
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.txt_retry),
                onClick = onRetry,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun EmptyViewPodPreview() {
    BasePreviewWrapper {
        EmptyViewPod(
            error = ErrorVO(
                code = "404",
                title = "Page Not Found",
                subtitle = "The page you are looking for does not exist."
            ),
            onRetry = {}
        )
    }
}