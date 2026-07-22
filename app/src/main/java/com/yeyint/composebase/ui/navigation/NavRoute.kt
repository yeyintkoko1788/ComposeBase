package com.yeyint.composebase.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * [icon] and [title] are marked [Transient] — they carry UI-only values (an [ImageVector] isn't
 * serializable, and display text shouldn't be baked into saved navigation state), so they're
 * excluded from the generated serializer used to persist the back stack.
 */
@Serializable
sealed class NavRoute(
    @Transient val path: String = "",
    @Transient val icon: ImageVector? = null,
    @Transient val title: String? = null
) {

    @Serializable
    data object Login : NavRoute("login")

    @Serializable
    data object LanguageSheet : NavRoute("language_sheet")

    @Serializable
    data object Home : NavRoute("home", Icons.Default.Home, "Home")

    @Serializable
    data object Calendar : NavRoute("calendar", Icons.Default.CalendarMonth, "Calendar")

    @Serializable
    data object Profile : NavRoute("profile", Icons.Default.AccountCircle, "Profile")

    @Serializable
    data class Search(val query: String) : NavRoute("search")
}