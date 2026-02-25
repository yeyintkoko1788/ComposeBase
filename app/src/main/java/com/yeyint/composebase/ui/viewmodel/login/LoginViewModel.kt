package com.yeyint.composebase.ui.viewmodel.login

import androidx.lifecycle.viewModelScope
import com.joygroup.onecheck.domain.model.ErrorVO
import com.joygroup.onecheck.domain.model.UserVO
import com.yeyint.composebase.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel(), LoginContract {
    private var _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    override val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    override fun login(email: String, password: String) {
        viewModelScope.launch {

            _loginUiState.value = LoginUiState.Loading
            delay(2000)
            try {
                if (email == "errorEmail@gmail.com"){
                    _loginUiState.value = LoginUiState.Error(
                        ErrorVO(
                            code = "401",
                            title = "Unauthorized",
                            subtitle = "Invalid credentials"
                        )
                    )
                }else{
                    val user = UserVO(
                        id = 1,
                        name = "John Doe",
                        employerID = "12345"
                    )
                    _loginUiState.value = LoginUiState.Success(user)
                }
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState.Error(
                    ErrorVO(
                        code = "404",
                        title = "Page Not Found",
                        subtitle = "The page you are looking for does not exist."
                    )
                )
            }
        }
    }
}
