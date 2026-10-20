package com.yeyint.composebase.ui.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yeyint.composebase.openPlayStoreListing
import com.yeyint.composebase.ui.common.MaintenanceScreen
import com.yeyint.composebase.ui.common.SessionExpiredBottomSheet
import com.yeyint.composebase.ui.common.UpdateAvailableBottomSheet
import com.yeyint.composebase.ui.viewmodel.MainViewModel
import com.yeyint.composebase.uitls.AppVersion
import com.yeyint.composebase.ui.navigation.BottomBarNav
import com.yeyint.composebase.ui.navigation.NavGraph3
import com.yeyint.composebase.ui.locale.AppLanguageProvider
import com.yeyint.composebase.ui.locale.LanguageController
import com.yeyint.composebase.ui.locale.LanguageManager
import com.yeyint.composebase.ui.locale.LocalLanguage
import com.yeyint.composebase.ui.locale.readSavedLanguage
import com.yeyint.composebase.ui.locale.withAppLanguage
import com.yeyint.composebase.ui.navigation.NavRoute
import com.yeyint.composebase.ui.screens.CalendarTopBar
import com.yeyint.composebase.ui.setEdgeToEdgeConfig
import com.yeyint.composebase.ui.theme.BaseTheme
import com.yeyint.composebase.ui.theme.ComposeBaseTheme
import com.yeyint.composebase.uitls.LOCALE_EN
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    private val topLevelRoutes: List<NavRoute> = listOf(NavRoute.Home, NavRoute.Calendar, NavRoute.Profile)

    /**
     * Runs before Hilt field injection, so the saved language is read straight from preferences
     * rather than through [languageManager]. This covers non-Compose surfaces (Toasts, system
     * dialogs); Compose itself is re-scoped by [AppLanguageProvider] below.
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withAppLanguage(readSavedLanguage(newBase)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()
        setContent {
            val language by languageManager.language.collectAsState()
            AppLanguageProvider(
                languageCode = language,
                onSelect = languageManager::setLanguage
            ) {
                MainScreen(topLevelRoutes)
            }
        }
    }
}

@Composable
private fun MainScreen(topLevelRoute: List<NavRoute>) {

    BaseTheme {
        val topLevelBackStack = remember { TopLevelBackStack<Any>(NavRoute.Login) }
        val mainViewModel: MainViewModel = hiltViewModel()
        val context = LocalContext.current

        // Takes over everything below — the whole NavGraph and every sheet — once any API call has
        // come back as maintenance. See MaintenanceEventBus's own doc for why this never clears.
        val maintenance by mainViewModel.maintenance.collectAsStateWithLifecycle()
        val maintenanceInfo = maintenance
        if (maintenanceInfo != null) {
            MaintenanceScreen(info = maintenanceInfo)
            return@BaseTheme
        }

        Scaffold(
            topBar = { AppTopBar(topLevelBackStack) },
            bottomBar = {
                // Check the screen actually on top of the back stack, not just which tab is
                // active — a screen pushed with `add()` (e.g. Search, launched from Home) stays
                // on the Home tab's stack, so `topLevelKey` alone would still read "Home" and
                // keep the bar visible even though a non-top-level screen is showing.
                val currentScreen = topLevelBackStack.backStack.lastOrNull()
                if (currentScreen in topLevelRoute) {
                    BottomBarNav(
                        topLevelRoute = topLevelRoute,
                        topLevelBackStack = topLevelBackStack
                    )
                }
            }
        ) { paddingValues ->
            NavGraph3(paddingValues, topLevelBackStack)
        }

        // A session that ended on its own (a 401 the token refresh couldn't recover). Explained
        // first, then the user is sent to Login — not bounced silently.
        val sessionExpired by mainViewModel.sessionExpired.collectAsStateWithLifecycle()
        if (sessionExpired) {
            SessionExpiredBottomSheet(
                onLogOut = {
                    mainViewModel.acknowledgeSessionExpired()
                    topLevelBackStack.resetTo(NavRoute.Login)
                }
            )
        }

        val versionUpdateInfo by mainViewModel.versionUpdateInfo.collectAsStateWithLifecycle()
        var showUpdateSheet by remember(versionUpdateInfo) { mutableStateOf(versionUpdateInfo != null) }
        val info = versionUpdateInfo
        if (showUpdateSheet && info != null) {
            UpdateAvailableBottomSheet(
                latestVersionLabel = "V${AppVersion.parse(info.latestVersion) ?: info.latestVersion}",
                releaseNotes = info.releaseNotes,
                isForceUpdate = info.forceUpdate,
                onUpdateNow = {
                    context.openPlayStoreListing()
                    if (!info.forceUpdate) showUpdateSheet = false
                },
                onLater = { showUpdateSheet = false }
            )
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

/**
 * Keeps a separate back stack per top-level destination (e.g. one per bottom-nav tab), so
 * switching tabs preserves each tab's own navigation history instead of sharing one flat stack.
 */
class TopLevelBackStack<T : Any>(initialStartKey: T) {

    private var startKey by mutableStateOf(initialStartKey)

    // Maintain a separate back stack for each top level route
    private val topLevelStacks = mutableMapOf<T, SnapshotStateList<T>>()

    private fun getStack(key: T): SnapshotStateList<T> =
        topLevelStacks.getOrPut(key) { mutableStateListOf(key) }

    // Expose the current top level route for consumers
    var topLevelKey by mutableStateOf(initialStartKey)
        private set

    // True only when the last navigation was a bottom-tab switch (addTopLevel)
    var isTabSwitching by mutableStateOf(false)
        private set

    // The combined back stack that NavDisplay will use
    val backStack = mutableStateListOf<T>()

    init {
        updateBackStack()
    }

    private fun updateBackStack() {
        backStack.apply {
            clear()
            // Always include the start tab's stack
            addAll(getStack(startKey))
            // If we're on a different tab, append its stack
            if (topLevelKey != startKey) {
                addAll(getStack(topLevelKey))
            }
        }
    }

    fun addTopLevel(key: T) {
        isTabSwitching = true
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T) {
        isTabSwitching = false
        getStack(topLevelKey).add(key)
        updateBackStack()
    }

    fun resetTo(key: T) {
        isTabSwitching = false
        topLevelStacks.clear()
        startKey = key
        topLevelKey = key
        updateBackStack()
    }

    fun popToRoot(key: T) {
        val stack = getStack(key)
        if (stack.size > 1) {
            val root = stack.first()
            stack.clear()
            stack.add(root)
            updateBackStack()
        }
    }

    fun canGoBack(): Boolean = backStack.size > 1

    fun tabBackStack(key: T): SnapshotStateList<T> = getStack(key)

    fun popUpTo(key: T, inclusive: Boolean = false) {
        val stack = getStack(topLevelKey)
        val index = stack.lastIndexOf(key)
        if (index >= 0) {
            val keepUntil = if (inclusive) index else index + 1
            while (stack.size > keepUntil) {
                stack.removeAt(stack.lastIndex)
            }
            updateBackStack()
        }
    }

    fun removeLast(onExit: (() -> Unit)? = null) {
        isTabSwitching = false
        val currentStack = getStack(topLevelKey)
        val currentRoute = currentStack.lastOrNull()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == topLevelKey) {
            if (topLevelKey != startKey) {
                topLevelKey = startKey
            } else {
                // At the root of the start stack — let the caller decide (e.g. finish activity)
                onExit?.invoke()
                return
            }
        } else if (currentStack.isNotEmpty()) {
            // Otherwise, just pop the current stack
            currentStack.removeAt(currentStack.lastIndex)
        }
        updateBackStack()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CompositionLocalProvider(LocalLanguage provides LanguageController(LOCALE_EN) {}) {
        ComposeBaseTheme {
            MainScreen(
                listOf(NavRoute.Home, NavRoute.Calendar, NavRoute.Profile)
            )
        }
    }
}
