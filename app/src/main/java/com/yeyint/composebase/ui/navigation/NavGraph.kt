@file:OptIn(ExperimentalMaterial3Api::class)

package com.yeyint.composebase.ui.navigation

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

@Composable()
fun NavGraph3(contentPadding: PaddingValues, topLevelBackStack: TopLevelBackStack<Any>){
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<Any>() }
    NavDisplay(modifier = Modifier.padding(contentPadding), backStack = topLevelBackStack.backStack,
        onBack = { topLevelBackStack.removeLast() },
        // In order to add the `ViewModelStoreNavEntryDecorator` (see comment below for why)
        // we also need to add the default `NavEntryDecorator`s as well. These provide
        // extra information to the entry's content to enable it to display correctly
        // and save its state.
        sceneStrategy = bottomSheetStrategy,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
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
                        topLevelBackStack.addTopLevel(NavRoute.Search(query))
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