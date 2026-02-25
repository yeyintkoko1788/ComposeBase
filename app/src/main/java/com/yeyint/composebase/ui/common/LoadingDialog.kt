package com.yeyint.composebase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.n011

@Composable
fun LoadingDialog(
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .testTag("loading_dialog")
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(16.dp),
            color = n011,
            tonalElevation = 6.dp // ⭐ important for Material3
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(n011),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview
@Composable
fun LoadingDialogPreview() {
    BasePreviewWrapper {
        LoadingDialog(modifier = Modifier)
    }
}
