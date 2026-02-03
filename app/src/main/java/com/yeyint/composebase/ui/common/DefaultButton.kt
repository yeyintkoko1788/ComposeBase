package com.yeyint.composebase.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.ui.theme.BasePreviewWrapper

@Composable
fun DefaultButton(
    text: String,
    onClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(5.dp))
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 200.dp, height = 70.dp)) {
        Text(text, fontSize = 40.sp)
    }
}

@Preview
@Composable
private fun DefaultButtonPreview() {
    BasePreviewWrapper {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(text = "Button", onClick = {})
        }
    }
}