package com.yeyint.composebase.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.yeyint.composebase.ui.screens.CalendarScreen
import com.yeyint.composebase.ui.screens.HomeScreen
import com.yeyint.composebase.ui.screens.LoginScreen
import com.yeyint.composebase.ui.screens.ProfileScreen
import com.yeyint.composebase.ui.screens.SearchScreen


@Composable
fun NavGraph(navController: NavHostController, contentPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        navigation(
            route = "auth",
            startDestination = NavRoute.Login.path
        ) {
            addLoginScreen(navController, this)
        }

        navigation(
            route = "main",
            startDestination = NavRoute.Home.path,
        ) {
            addHomeScreen(navController, this)
            addProfileScreen(navController, this)
            addSearchScreen(navController, this)
            addCalendarScreen(this, paddingValues = contentPadding)
        }
    }
}

private fun addLoginScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = NavRoute.Login.path) {
        LoginScreen(
            navigateToHome = {
                navController.navigate("main") {
                    popUpTo("auth") { inclusive = true }
                }
            }
        )
    }
}

private fun addHomeScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = NavRoute.Home.path) {

        HomeScreen(
            navigateToProfile = { id, showDetails ->
                navController.navigate(NavRoute.Profile.withArgs(id.toString(), showDetails.toString()))
            },
            navigateToSearch = { query ->
                navController.navigate(NavRoute.Search.withArgs(query))
            },
            popBackStack = { navController.popBackStack() },
            popUpToLogin= { popUpToLogin(navController) },
        )
    }
}

private fun popUpToLogin(navController: NavHostController) {
    navController.navigate("auth") {
        popUpTo("main") { inclusive = true }
    }
}

private fun addProfileScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.Profile.withArgsFormat(NavRoute.Profile.id, NavRoute.Profile.showDetails),
        arguments = listOf(
            navArgument(NavRoute.Profile.id) {
                type = NavType.IntType
            }
            ,
            navArgument(NavRoute.Profile.showDetails) {
                type = NavType.BoolType
            }
        )
    ) { navBackStackEntry ->

        val args = navBackStackEntry.arguments

        ProfileScreen(
            id = args?.getInt(NavRoute.Profile.id)!!,
            showDetails = args.getBoolean(NavRoute.Profile.showDetails),
            popBackStack = { navController.popBackStack() },
            popUpToLogin = { popUpToLogin(navController) }
        )
    }
}

private fun addCalendarScreen(
    navGraphBuilder: NavGraphBuilder,
    paddingValues: PaddingValues
) {
    navGraphBuilder.composable(route = NavRoute.Calendar.path) {
        CalendarScreen(paddingValues)
    }
}

private fun addSearchScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.navigation(
        route = NavRoute.Search.path, // "search"
        startDestination = NavRoute.Search.withArgsFormat(NavRoute.Search.query)
    ) {

        composable(
            route = NavRoute.Search.withArgsFormat(NavRoute.Search.query),
            arguments = listOf(
                navArgument(NavRoute.Search.query) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { navBackStackEntry ->

            val args = navBackStackEntry.arguments

            SearchScreen(
                query = args?.getString(NavRoute.Search.query),
                popBackStack = { navController.popBackStack() },
                popUpToLogin = { popUpToLogin(navController) }
            )
        }
    }
}
