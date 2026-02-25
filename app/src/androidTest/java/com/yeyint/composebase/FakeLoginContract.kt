package com.yeyint.composebase

import com.joygroup.onecheck.domain.model.ErrorVO
import com.joygroup.onecheck.domain.model.UserVO
import com.yeyint.composebase.ui.viewmodel.login.LoginContract
import com.yeyint.composebase.ui.viewmodel.login.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeLoginContract : LoginContract {

    private val _state =
        MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    override val loginUiState =
        _state.asStateFlow()

    override fun login(email: String, password: String) {}

    fun emitSuccess() {
        _state.value = LoginUiState.Success(
            UserVO(1, "John", "12345")
        )
    }

    fun emitLoading() {
        _state.value = LoginUiState.Loading
    }

    fun emitError() {
        _state.value = LoginUiState.Error(
            ErrorVO("401", "Unauthorized", "Invalid credentials")
        )
    }
}
