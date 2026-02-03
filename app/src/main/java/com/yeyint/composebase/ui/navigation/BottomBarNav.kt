package com.yeyint.composebase.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBarNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = when (currentRoute) {
        // List all routes that should have a bottom bar
        NavRoute.Home.path -> true
        NavRoute.Search.withArgsFormat(NavRoute.Search.query) -> true
        else -> false // All other routes will not show the bar
    }

    if (shouldShowBottomBar) {
        NavigationBar {

            val homeSelected = currentRoute == NavRoute.Home.path
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = NavRoute.Home.path
                    )
                },
                selected = homeSelected,
                onClick = {
                    if(!homeSelected) {
                        navController.navigate(NavRoute.Home.path) {
                            popUpTo(NavRoute.Home.path) { inclusive = true }
                        }
                    }
                },
                label = {Text(NavRoute.Home.path)}
            )

            val searchSelected =  currentRoute == NavRoute.Search.withArgsFormat(NavRoute.Search.query)
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = NavRoute.Home.path
                    )
                },
                selected = searchSelected,
                onClick = {
                    if(!searchSelected) {
                        navController.navigate(NavRoute.Search.withArgs("Liang Moi"))
                    }
                },
                label = { Text(NavRoute.Search.path) }
            )
        }
    }else{
        return
    }
}