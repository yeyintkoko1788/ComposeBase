package com.yeyint.composebase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.text_inverted

/** A circular translucent-black icon button on a colored header — back buttons, support/help
 * icons, etc. Shared across every screen with a plain (non-collapsing) toolbar. */
@Composable
fun HeaderIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(Color.Black.copy(alpha = 0.15f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = text_inverted
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HeaderIconButtonPreview() {
    BasePreviewWrapper {
        HeaderIconButton(
            iconRes = R.drawable.ic_back,
            contentDescription = "Back",
            onClick = {}
        )
    }
}
