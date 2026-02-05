package com.yeyint.composebase.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yeyint.composebase.ui.theme.BasePreviewWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    BasePreviewWrapper() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            CalendarScreen()
        }
    }
}