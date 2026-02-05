package com.yeyint.composebase.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBarNav(navController: NavHostController) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == NavRoute.Home.path,
            onClick = {
                navController.navigate(NavRoute.Home.path) {
                    popUpTo(NavRoute.Home.path) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoute.Calendar.path,
            onClick = {
                navController.navigate(NavRoute.Calendar.path) {
                    popUpTo(NavRoute.Home.path) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.CalendarMonth, null) },
            label = { Text("Calendar") }
        )

        NavigationBarItem(
            selected = currentRoute?.startsWith(NavRoute.Search.path) == true,
            onClick = {
                navController.navigate(
                    NavRoute.Search.withArgs("Liang Moi")
                ) {
                    popUpTo(NavRoute.Home.path) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Search, null) },
            label = { Text("Search") }
        )
    }
}


