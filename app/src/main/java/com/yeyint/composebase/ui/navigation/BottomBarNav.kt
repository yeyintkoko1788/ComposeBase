package com.yeyint.composebase.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import com.yeyint.composebase.ui.app.TopLevelBackStack

@Composable
fun BottomBarNav(
    topLevelRoute: List<NavRoute>,
    topLevelBackStack: TopLevelBackStack<Any>
) {
    NavigationBar {
        topLevelRoute.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon!!,
                        contentDescription = null
                    )
                },
                //label = { Text(screen.title) },
                selected = topLevelBackStack.topLevelKey == screen,
                onClick = { topLevelBackStack.addTopLevel(screen) }
            )
        }
    }
}
