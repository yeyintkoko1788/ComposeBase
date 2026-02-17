package com.yeyint.composebase.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.ui.viewmodel.SearchViewModel
import com.yeyint.composebase.ui.common.PrimaryButton
import com.yeyint.composebase.ui.navigation.NavRoute
import com.yeyint.composebase.ui.theme.BasePreviewWrapper

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    popBackStack: () -> Unit,
    popUpToLogin: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Search Screen", fontSize = 40.sp)

        Spacer(modifier = Modifier.height(5.dp))
        Text("Query: ${viewModel.navKey.query}", fontSize = 40.sp)

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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    BasePreviewWrapper() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchScreen(
                popBackStack = {},
                popUpToLogin = {},
                viewModel = SearchViewModel(
                    NavRoute.Search("test")
                )
            )
        }
    }
}