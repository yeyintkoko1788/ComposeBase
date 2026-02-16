package com.yeyint.composebase.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.ui.common.PrimaryButton
import com.yeyint.composebase.ui.theme.BasePreviewWrapper

@Composable
fun HomeScreen(
    navigateToProfile: (Int, Boolean) -> Unit,
    navigateToSearch: (String) -> Unit,
    popBackStack: () -> Unit,
    popUpToLogin: () -> Unit,
) {

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Home Screen", fontSize = 40.sp)

        PrimaryButton(
            text = "Profile",
            onClick = { navigateToProfile(7, true) }
        )

        PrimaryButton(
            text = "Search",
            onClick = { navigateToSearch("liang moi") }
        )

        PrimaryButton(
            text = "Back",
            onClick = popBackStack
        )

        PrimaryButton(
            text = "Log Out",
            onClick = popUpToLogin
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    BasePreviewWrapper() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeScreen(
                navigateToProfile = { _,_ -> },
                navigateToSearch = {},
                popBackStack = {},
                popUpToLogin = {})
       }
    }
}