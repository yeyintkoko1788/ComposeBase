package com.yeyint.composebase.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import kotlinx.coroutines.delay

/**
 * A short-lived success toast that auto-dismisses after [durationMillis].
 *
 * **Placement:** these are plain composables, not a `SnackbarHost` — they occupy layout space
 * wherever they are placed. Render them as a child of a `Box` with
 * `Modifier.align(Alignment.BottomCenter)` so they float over the content. Dropping one into a
 * `Column` between a list and a composer/footer makes that footer visibly jump down while the
 * snackbar is showing and back up when it dismisses.
 */
@Composable
fun SuccessSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 1000L
) {
    LaunchedEffect(message) {
        delay(durationMillis)
        onDismiss()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF4CAF50)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = message, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Preview
@Composable
fun SuccessSnackbarPreview() {
    BasePreviewWrapper {
        SuccessSnackbar(message = "Success!", onDismiss = {})
    }
}
