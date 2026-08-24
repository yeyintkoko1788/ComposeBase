package com.yeyint.composebase

import app.cash.turbine.test
import com.yeyint.composebase.domain.exception.GenericErrorMessageFactory
import com.yeyint.composebase.network.components.MyPreference
import com.yeyint.composebase.ui.viewmodel.login.LoginUiState
import com.yeyint.composebase.ui.viewmodel.login.LoginViewModel
import io.mockk.mockk
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

    /**
     * The base-class dependencies aren't exercised by the login stub, so relaxed mocks are enough
     * here. Swap them for real fakes once [LoginViewModel] starts using them for error mapping.
     */
    private fun createViewModel() = LoginViewModel(
        flowGenericErrorMessageFactory = mockk<GenericErrorMessageFactory>(relaxed = true),
        myPreference = mockk<MyPreference>(relaxed = true)
    )

    @Before
    fun setup() {
        viewModel = createViewModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login with valid email emits Loading then Success`() = runTest {

        val viewModel = createViewModel()

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

        val viewModel = createViewModel()

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