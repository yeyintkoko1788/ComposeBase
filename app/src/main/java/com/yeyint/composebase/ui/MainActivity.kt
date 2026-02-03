package com.yeyint.composebase.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.yeyint.composebase.ui.navigation.BottomBarNav
import com.yeyint.composebase.ui.navigation.NavGraph
import com.yeyint.composebase.ui.theme.BaseTheme
import com.yeyint.composebase.ui.theme.ComposeBaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    BaseTheme {

        val navController = rememberNavController()

        Scaffold(
            bottomBar = { BottomBarNav(navController = navController) }
        ) { paddingValues ->
            NavGraph(
                modifier = Modifier.padding(paddingValues),
                navController = navController
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeBaseTheme {
        MainScreen()
    }
}