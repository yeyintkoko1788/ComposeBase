package com.yeyint.composebase

import app.cash.turbine.test
import com.yeyint.composebase.ui.viewmodel.login.LoginUiState
import com.yeyint.composebase.ui.viewmodel.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login with valid email emits Loading then Success`() = runTest {

        val viewModel = LoginViewModel()

        viewModel.login("admin@gmail.com", "123456")

        viewModel.loginUiState.test {

            // Initial state
            assert(awaitItem() is LoginUiState.Idle)

            // Loading state
            assert(awaitItem() is LoginUiState.Loading)

            // Skip delay(2000)
            advanceTimeBy(2000)

            // Success state
            val success = awaitItem()
            assert(success is LoginUiState.Success)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login with error email emits Loading then Error`() = runTest {

        val viewModel = LoginViewModel()

        viewModel.login("errorEmail@gmail.com", "123456")

        viewModel.loginUiState.test {

            // Initial
            assert(awaitItem() is LoginUiState.Idle)

            // Loading
            assert(awaitItem() is LoginUiState.Loading)

            // Skip delay
            advanceTimeBy(2000)

            // Error
            val error = awaitItem()
            assert(error is LoginUiState.Error)
        }
    }
}