package com.yeyint.composebase.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(val path: String,val icon: ImageVector? = null) {

    object Login: NavRoute("login")

    object Home: NavRoute("home",Icons.Default.Home)

    object Calendar : NavRoute("calendar",Icons.Default.CalendarMonth)

    object Profile: NavRoute("profile") {
        val id = "id"
        val showDetails = "showDetails"
    }

    object Search: NavRoute("search",Icons.Default.Search) {
        val query = "query"
    }

    // build navigation path (for screen navigation)
    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach{ arg ->
                append("/$arg")
            }
        }
    }

    // build and setup route format (in navigation graph)
    fun withArgsFormat(vararg args: String) : String {
        return buildString {
            append(path)
            args.forEach{ arg ->
                append("/{$arg}")
            }
        }
    }
}