package com.yeyint.composebase.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.afternoon_color
import com.yeyint.composebase.ui.theme.error_default
import com.yeyint.composebase.ui.theme.semiboldBodySmall
import kotlinx.coroutines.delay

/**
 * A dismissible error/network banner. When [isNetworkError] is true and [onRetry] is supplied,
 * the banner stays up (no auto-dismiss) and shows a Retry action instead of a close icon.
 *
 * **Placement:** these are plain composables, not a `SnackbarHost` — they occupy layout space
 * wherever they are placed. Render them as a child of a `Box` with
 * `Modifier.align(Alignment.BottomCenter)` so they float over the content. Dropping one into a
 * `Column` between a list and a composer/footer makes that footer visibly jump down while the
 * snackbar is showing and back up when it dismisses.
 */
@Composable
fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3000L,
    isNetworkError: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    val dismissDelay = if (isNetworkError && onRetry != null) {
        Long.MAX_VALUE
    } else {
        durationMillis
    }

    LaunchedEffect(message, isNetworkError) {
        if (dismissDelay != Long.MAX_VALUE) {
            delay(dismissDelay)
            onDismiss()
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        color = if (isNetworkError) afternoon_color else error_default
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isNetworkError) Icons.Default.WifiOff else Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            if (isNetworkError && onRetry != null) {
                TextButton(
                    onClick = {
                        onRetry()
                        onDismiss()
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.txt_retry),
                        color = Color.White,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.semiboldBodySmall
                    )
                }
            } else {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ErrorSnackbarPreview() {
    BasePreviewWrapper {
        ErrorSnackbar(message = "Invalid credentials. Please try again.", onDismiss = {})
    }
}
