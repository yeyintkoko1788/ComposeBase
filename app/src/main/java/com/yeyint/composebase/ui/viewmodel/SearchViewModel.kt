package com.yeyint.composebase.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.yeyint.composebase.ui.navigation.NavRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
class SearchViewModel @AssistedInject constructor(
    @Assisted val navKey: NavRoute.Search
) : ViewModel(){
    @AssistedFactory
    interface Factory {
        fun create(navKey: NavRoute.Search): SearchViewModel
    }
}