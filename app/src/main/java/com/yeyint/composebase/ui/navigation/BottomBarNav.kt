package com.yeyint.composebase.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.yeyint.composebase.ui.app.TopLevelBackStack
import com.yeyint.composebase.ui.theme.neutral_text

@Composable
fun BottomBarNav(
    topLevelRoute: List<NavRoute>,
    topLevelBackStack: TopLevelBackStack<Any>
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        topLevelRoute.forEach { screen ->
            val icon = screen.icon ?: return@forEach
            val isSelected = topLevelBackStack.topLevelKey == screen

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.title,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else neutral_text
                    )
                },
                label = screen.title?.let {
                    {
                        Text(
                            text = it,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else neutral_text
                        )
                    }
                },
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        topLevelBackStack.popToRoot(screen)
                    } else {
                        topLevelBackStack.addTopLevel(screen)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
