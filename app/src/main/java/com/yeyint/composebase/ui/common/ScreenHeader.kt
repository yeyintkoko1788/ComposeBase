package com.yeyint.composebase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.semiboldBodyLarge
import com.yeyint.composebase.ui.theme.surface_brand
import com.yeyint.composebase.ui.theme.text_inverted

/**
 * A plain fixed toolbar — back button + centered title on a colored bar — for any screen that
 * has no route/status info to keep pinned while scrolling (unlike [CompletedOrderDetailScreen]'s
 * collapsing header, which does). Shared so this shape doesn't get re-copied per screen.
 */
@Composable
fun ScreenHeader(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(surface_brand)
            .statusBarsPadding()
            .height(56.dp)
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
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderPreview() {
    BasePreviewWrapper {
        Surface {
            ScreenHeader(title = "Edit Information", onBack = {})
        }
    }
}
