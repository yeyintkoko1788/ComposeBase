package com.yeyint.composebase.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(val path: String,val icon: ImageVector? = null) {

    object Login: NavRoute("login")

    object Home: NavRoute("home",Icons.Default.Home)

    object Calendar : NavRoute("calendar",Icons.Default.CalendarMonth)

    object Profile: NavRoute("profile",Icons.Default.AccountCircle)

    data class Search(val query : String): NavRoute("search")
}