package com.yeyint.composebase.ui.viewmodel.login

import com.joygroup.onecheck.domain.model.ErrorVO
import com.joygroup.onecheck.domain.model.UserVO
import kotlinx.coroutines.flow.StateFlow

interface LoginContract {
    val loginUiState: StateFlow<LoginUiState>

    fun login(
        email: String,
        password: String
    )
}

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: UserVO) : LoginUiState
    data class Error(val error : ErrorVO) : LoginUiState
}
