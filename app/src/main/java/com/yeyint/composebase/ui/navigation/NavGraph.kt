package com.yeyint.composebase.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.yeyint.composebase.ui.app.TopLevelBackStack
import com.yeyint.composebase.ui.screens.CalendarScreen
import com.yeyint.composebase.ui.screens.HomeScreen
import com.yeyint.composebase.ui.screens.LoginScreen
import com.yeyint.composebase.ui.screens.ProfileScreen
import com.yeyint.composebase.ui.screens.SearchScreen

@Composable()
fun NavGraph3(contentPadding: PaddingValues, topLevelBackStack: TopLevelBackStack<Any>){
    NavDisplay(modifier = Modifier.padding(contentPadding), backStack = topLevelBackStack.backStack,
        onBack = { topLevelBackStack.removeLast() },
        entryProvider = entryProvider {
            entry<NavRoute.Login> {
                LoginScreen(
                    navigateToHome = {
                        topLevelBackStack.resetTo(NavRoute.Home)
                    }
                )
            }
            entry<NavRoute.Profile> {
                ProfileScreen(
                    id = 1,
                    showDetails = true,
                    popBackStack = { topLevelBackStack.removeLast() },
                    popUpToLogin = { topLevelBackStack.resetTo(NavRoute.Login) }
                )
            }
            entry<NavRoute.Home> {
                HomeScreen(
                    navigateToProfile = { id, showDetails ->
                        topLevelBackStack.add(
                            NavRoute.Profile
                        )
                    },
                    navigateToSearch = { query ->
                        topLevelBackStack.addTopLevel(NavRoute.Search)
                    },
                    popBackStack = { topLevelBackStack.removeLast() },
                    popUpToLogin = {  topLevelBackStack.resetTo(NavRoute.Login) },
                )
            }
            entry<NavRoute.Calendar> {
                CalendarScreen(contentPadding)
            }
            entry<NavRoute.Search> {
                SearchScreen(
                    query = "test",
                    popBackStack = { topLevelBackStack.removeLast() },
                    popUpToLogin = {
                        topLevelBackStack.resetTo(NavRoute.Login)
                    }
                )
            }
        })
}