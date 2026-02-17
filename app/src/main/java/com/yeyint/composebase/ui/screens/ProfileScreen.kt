package com.yeyint.composebase.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.ui.common.PrimaryButton
import com.yeyint.composebase.ui.theme.BasePreviewWrapper

@Composable
fun ProfileScreen(
    popBackStack: () -> Unit,
    popUpToLogin: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Hello User", fontSize = 40.sp)

        PrimaryButton(
            text = "Back",
            onClick = popBackStack,
            modifier = Modifier.padding(8.dp)
        )
        PrimaryButton(
            text = "Log Out",
            onClick = popUpToLogin,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    BasePreviewWrapper {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            ProfileScreen(
                popBackStack = {},
                popUpToLogin = {}
            )
        }
    }
}