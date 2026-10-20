@file:OptIn(ExperimentalMaterial3Api::class)

package com.yeyint.composebase.ui.navigation

import androidx.activity.BackEventCompat
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.yeyint.composebase.ui.viewmodel.SearchViewModel
import com.yeyint.composebase.ui.app.TopLevelBackStack
import com.yeyint.composebase.ui.common.BottomSheetSceneStrategy
import com.yeyint.composebase.ui.common.LanguageBottomSheet
import com.yeyint.composebase.ui.screens.CalendarScreen
import com.yeyint.composebase.ui.screens.HomeScreen
import com.yeyint.composebase.ui.screens.LoginScreen
import com.yeyint.composebase.ui.screens.ProfileScreen
import com.yeyint.composebase.ui.screens.SearchScreen
import com.yeyint.composebase.ui.viewmodel.HomeViewModel
import com.yeyint.composebase.ui.viewmodel.login.LoginViewModel
import kotlin.collections.removeLast

// The screen underneath moves a third as far as the one on top — iOS's push/pop parallax.
private const val ParallaxDivisor = 3

@Composable()
fun NavGraph3(contentPadding: PaddingValues, topLevelBackStack: TopLevelBackStack<Any>){
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<Any>() }
    NavDisplay(modifier = Modifier.padding(contentPadding), backStack = topLevelBackStack.backStack,
        onBack = { topLevelBackStack.removeLast() },
        // In order to add the `ViewModelStoreNavEntryDecorator` (see comment below for why)
        // we also need to add the default `NavEntryDecorator`s as well. These provide
        // extra information to the entry's content to enable it to display correctly
        // and save its state.
        sceneStrategies = listOf(bottomSheetStrategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        // Bottom-tab switches (Home/Calendar/Profile) get a quick fade + scale-in; every other
        // push/pop (e.g. Login -> Home, drilling into a detail screen) gets an iOS-style slide:
        // the moving screen travels the full width while the one beneath shifts only a third of it
        // (parallax), so the two never move in lock-step.
        transitionSpec = {
            if (topLevelBackStack.isTabSwitching) {
                fadeIn(
                    animationSpec = tween(100, delayMillis = 80)
                ) + scaleIn(
                    initialScale = 0.92f, animationSpec = tween(100, delayMillis = 80)
                ) togetherWith fadeOut(animationSpec = tween(120))
            } else {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(300)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it / ParallaxDivisor }, animationSpec = tween(300)
                )
            }
        },
        popTransitionSpec = {
            if (topLevelBackStack.isTabSwitching) {
                fadeIn(
                    animationSpec = tween(220, delayMillis = 80)
                ) + scaleIn(
                    initialScale = 0.92f, animationSpec = tween(220, delayMillis = 80)
                ) togetherWith fadeOut(animationSpec = tween(120))
            } else {
                // Below (-1f), not above: the screen being revealed is the target, which would
                // otherwise be drawn over the one sliding away and hide it.
                ContentTransform(
                    targetContentEnter = slideInHorizontally(
                        initialOffsetX = { -it / ParallaxDivisor }, animationSpec = tween(300)
                    ),
                    initialContentExit = slideOutHorizontally(
                        targetOffsetX = { it }, animationSpec = tween(300)
                    ),
                    targetContentZIndex = -1f
                )
            }
        },
        // The swipe-back preview — same slide and parallax as popTransitionSpec above, so dragging
        // back looks like tapping back, just under the finger. No isTabSwitching branch: a back
        // gesture is never a tab tap, and that flag can still be true from the last tab switch.
        // Linear so the screen tracks the finger 1:1 rather than easing against it. The slide
        // mirrors for a right-edge swipe, where the finger drags the screen away to the left.
        predictivePopTransitionSpec = { swipeEdge ->
            val direction = if (swipeEdge == BackEventCompat.EDGE_RIGHT) -1 else 1
            ContentTransform(
                targetContentEnter = slideInHorizontally(
                    initialOffsetX = { -it / ParallaxDivisor * direction },
                    animationSpec = tween(300, easing = LinearEasing)
                ),
                initialContentExit = slideOutHorizontally(
                    targetOffsetX = { it * direction }, animationSpec = tween(300, easing = LinearEasing)
                ),
                targetContentZIndex = -1f
            )
        },
        entryProvider = entryProvider {
            entry<NavRoute.Login> {
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(contract = viewModel, navigateToHome = {
                    topLevelBackStack.resetTo(NavRoute.Home)
                }, onTermsClicked = {

                }, onPrivacyPolicyClicked = {

                }, onLanguageChangeClicked = {
                    topLevelBackStack.add(NavRoute.LanguageSheet)
                })
            }
            entry<NavRoute.LanguageSheet>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { key ->
                LanguageBottomSheet { topLevelBackStack.removeLast() }
            }
            entry<NavRoute.Profile> {
                ProfileScreen(
                    popBackStack = { topLevelBackStack.removeLast() },
                    popUpToLogin = { topLevelBackStack.resetTo(NavRoute.Login) }
                )
            }
            entry<NavRoute.Home> {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    navigateToProfile = {
                        topLevelBackStack.addTopLevel(
                            NavRoute.Profile
                        )
                    },
                    navigateToSearch = { query ->
                        topLevelBackStack.add(NavRoute.Search(query))
                    },
                    popBackStack = { topLevelBackStack.removeLast() },
                    popUpToLogin = {  topLevelBackStack.resetTo(NavRoute.Login) },
                )
            }
            entry<NavRoute.Calendar> {
                CalendarScreen()
            }
            entry<NavRoute.Search> { key ->
                val viewModel = hiltViewModel<SearchViewModel, SearchViewModel.Factory>(
                    // Note: We need a new ViewModel for every new RouteB instance. Usually
                    // we would need to supply a `key` String that is unique to the
                    // instance, however, the ViewModelStoreNavEntryDecorator (supplied
                    // above) does this for us, using `NavEntry.contentKey` to uniquely
                    // identify the viewModel.
                    //
                    // tl;dr: Make sure you use rememberViewModelStoreNavEntryDecorator()
                    // if you want a new ViewModel for each new navigation key instance.
                    creationCallback = { factory ->
                        factory.create(key)
                    }
                )
                SearchScreen(viewModel = viewModel,
                    popBackStack = { topLevelBackStack.removeLast() },
                    popUpToLogin = {
                        topLevelBackStack.resetTo(NavRoute.Login)
                    }
                )
            }
        })
}