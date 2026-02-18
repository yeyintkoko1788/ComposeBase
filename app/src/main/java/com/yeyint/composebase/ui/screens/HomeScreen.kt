package com.yeyint.composebase.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.ui.common.DefaultEditText
import com.yeyint.composebase.ui.common.PrimaryButton
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToProfile: () -> Unit,
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
            onClick = { navigateToProfile() },
            modifier = Modifier.padding(8.dp)
        )
        var text1 by remember { mutableStateOf("") }
        DefaultEditText(
            modifier = Modifier.padding(8.dp),
            value = text1,
            onValueChange = { text1 = it },
            label = "Search Key"
        )

        PrimaryButton(
            modifier = Modifier.padding(8.dp),
            text = "Search",
            onClick = { navigateToSearch(text1) }
        )
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

        var password by remember { mutableStateOf("") }
        DefaultEditText(
            modifier = Modifier.padding(8.dp),
            value = password,
            onValueChange = { password = it },
            label = "Enter Password",
            isPasswordField = true
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    BasePreviewWrapper() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeScreen(
                viewModel = HomeViewModel(),
                navigateToProfile = {},
                navigateToSearch = {},
                popBackStack = {},
                popUpToLogin = {})
       }
    }
}