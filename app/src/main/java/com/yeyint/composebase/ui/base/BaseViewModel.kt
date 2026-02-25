package com.yeyint.composebase.ui.base

import androidx.lifecycle.ViewModel
import com.yeyint.composebase.domain.exception.GenericErrorMessageFactory
import com.yeyint.composebase.network.components.MyPreference
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseViewModel : ViewModel()  {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    @Inject
    lateinit var flowGenericErrorMessageFactory: GenericErrorMessageFactory

    @Inject
    lateinit var myPreference: MyPreference
}