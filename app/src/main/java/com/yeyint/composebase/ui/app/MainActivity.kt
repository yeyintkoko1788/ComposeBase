package com.yeyint.composebase.ui.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.yeyint.composebase.network.showLogD
import com.yeyint.composebase.ui.navigation.BottomBarNav
import com.yeyint.composebase.ui.navigation.NavGraph
import com.yeyint.composebase.ui.navigation.NavRoute
import com.yeyint.composebase.ui.screens.CalendarTopBar
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

        val appState = rememberAppState()

        Scaffold(
            topBar = { AppTopBar(appState) },
            bottomBar = {
                if (shouldShowBottomBar(appState)) {
                    BottomBarNav(navController = appState.navController)
                }
            }
        ) { paddingValues ->
            NavGraph(
                modifier = Modifier.padding(paddingValues),
                navController = appState.navController
            )
        }
    }
}

@Composable
fun AppTopBar(appState: AppState) {
    when (appState.currentRoute) {
        NavRoute.Calendar.path -> CalendarTopBar()
        else -> Unit // Login, Splash, etc
    }
}

@Composable
fun shouldShowBottomBar(appState: AppState): Boolean {
    val destination = appState.currentDestination

    return destination
        ?.hierarchy
        ?.any { it.route in listOf(
            NavRoute.Home.path,
            NavRoute.Calendar.path,
            NavRoute.Search.path
        ) } == true
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeBaseTheme {
        MainScreen()
    }
}