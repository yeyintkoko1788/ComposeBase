package com.yeyint.composebase.ui.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yeyint.composebase.ui.navigation.BottomBarNav
import com.yeyint.composebase.ui.navigation.NavGraph3
import com.yeyint.composebase.ui.navigation.NavRoute
import com.yeyint.composebase.ui.screens.CalendarTopBar
import com.yeyint.composebase.ui.setEdgeToEdgeConfig
import com.yeyint.composebase.ui.theme.BaseTheme
import com.yeyint.composebase.ui.theme.ComposeBaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val topLevelRoutes: List<NavRoute> = listOf(NavRoute.Home, NavRoute.Calendar, NavRoute.Profile)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()
        setContent {
            MainScreen(topLevelRoutes)
        }
    }
}

@Composable
private fun MainScreen(topLevelRoute: List<NavRoute>) {

    BaseTheme {
        val topLevelBackStack = remember { TopLevelBackStack<Any>(NavRoute.Login) }

        Scaffold(
            topBar = { AppTopBar(topLevelBackStack) },
            bottomBar = {
                if (topLevelBackStack.topLevelKey in topLevelRoute) {
                    BottomBarNav(
                        topLevelRoute = topLevelRoute,
                        topLevelBackStack = topLevelBackStack
                    )
                }
            }
        ) { paddingValues ->
            NavGraph3(paddingValues, topLevelBackStack)
        }
    }
}

@Composable
fun AppTopBar(topLevelBackStack: TopLevelBackStack<Any>) {
    when (topLevelBackStack.backStack.lastOrNull()) {
        NavRoute.Calendar -> CalendarTopBar()
        else -> Unit // Login, Splash, etc
    }
}

class TopLevelBackStack<T : Any>(startKey: T) {

    // Maintain a stack for each top level route
    private var topLevelStacks: LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )

    // Expose the current top level route for consumers
    var topLevelKey by mutableStateOf(startKey)
        private set

    // Expose the back stack so it can be rendered by the NavDisplay
    val backStack = mutableStateListOf(startKey)

    private fun updateBackStack() {
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }
    }

    fun addTopLevel(key: T) {

        // If the top level doesn't exist, add it
        if (topLevelStacks[key] == null) {
            topLevelStacks[key] = mutableStateListOf(key)
        } else {
            // Otherwise just move it to the end of the stacks
            topLevelStacks.apply {
                remove(key)?.let {
                    put(key, it)
                }
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T) {
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun resetTo(key: T) {
        topLevelStacks.clear()
        topLevelStacks[key] = mutableStateListOf(key)
        topLevelKey = key
        updateBackStack()
    }

    fun removeLast() {
        val removedKey = topLevelStacks[topLevelKey]?.removeLastOrNull()
        // If the removed key was a top level key, remove the associated top level stack
        topLevelStacks.remove(removedKey)
        if (topLevelStacks.isNotEmpty()) {
            topLevelKey = topLevelStacks.keys.last()
        }
        updateBackStack()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeBaseTheme {
        MainScreen(
            listOf(NavRoute.Home, NavRoute.Calendar, NavRoute.Profile)
        )
    }
}
