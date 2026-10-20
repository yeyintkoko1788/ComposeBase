package com.yeyint.composebase.ui.base

import androidx.lifecycle.ViewModel
import com.yeyint.composebase.domain.exception.GenericErrorMessageFactory
import com.yeyint.composebase.network.components.MyPreference

/**
 * Shared base for feature ViewModels.
 *
 * Dependencies are taken as constructor parameters, not `@Inject lateinit var` fields: Hilt only
 * performs constructor injection on `@HiltViewModel` classes and never field-injects them, so
 * injected fields here would stay uninitialised and throw on first access. Subclasses declare the
 * dependencies in their own `@Inject constructor` and pass them up.
 */
abstract class BaseViewModel(
    protected val flowGenericErrorMessageFactory: GenericErrorMessageFactory,
    protected val myPreference: MyPreference
) : ViewModel() {
}
