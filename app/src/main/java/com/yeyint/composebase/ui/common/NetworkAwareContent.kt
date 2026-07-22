package com.yeyint.composebase.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.joygroup.onecheck.domain.model.ErrorVO
import com.yeyint.composebase.ui.theme.BasePreviewWrapper

/**
 * Shows [content] while [isConnected] is true, otherwise shows an [EmptyViewPod] no-internet
 * state. Automatically re-invokes [onRetry] the moment connectivity comes back after having
 * been offline, so the caller can silently refresh instead of forcing a manual retry tap.
 *
 * Wire [isConnected] up to [NetworkConnectivityObserver.observe] (e.g. via
 * `collectAsStateWithLifecycle`).
 */
@Composable
fun NetworkAwareContent(
    isConnected: Boolean,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    var wasOffline by remember { mutableStateOf(false) }

    LaunchedEffect(isConnected) {
        if (isConnected && wasOffline) {
            onRetry()
        }
        wasOffline = !isConnected
    }

    if (isConnected) {
        content()
    } else {
        EmptyViewPod(
            modifier = modifier,
            error = ErrorVO(
                code = "NO_INTERNET",
                title = "No internet connection",
                subtitle = "Please check your connection and try again."
            ),
            onRetry = onRetry,
            showRetry = true
        )
    }
}

@Preview
@Composable
fun NetworkAwareContentPreview() {
    BasePreviewWrapper {
        NetworkAwareContent(isConnected = false, onRetry = {}) {}
    }
}
