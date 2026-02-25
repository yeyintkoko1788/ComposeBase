package com.yeyint.composebase

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.yeyint.composebase.ui.screens.LoginScreen
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeContract = FakeLoginContract()

    @Test
    fun loginSuccess_navigateToHome_called() {

        var navigateCalled = false

        composeTestRule.setContent {

            LoginScreen(
                contract = fakeContract,
                navigateToHome = {
                    navigateCalled = true
                },
                onTermsClicked = {},
                onPrivacyPolicyClicked = {}
            )
        }

        composeTestRule.runOnIdle {
            fakeContract.emitSuccess()
        }

        composeTestRule.waitForIdle()

        assert(navigateCalled)
    }

    @Test
    fun loadingDialog_isVisible_whenLoginStateIsLoading() {

        composeTestRule.setContent {
            LoginScreen(
                contract = fakeContract,
                navigateToHome = {},
                onTermsClicked = {},
                onPrivacyPolicyClicked = {},
                onLanguageChangeClicked = {}
            )
        }

        composeTestRule.runOnUiThread {
            fakeContract.emitLoading()
        }

        composeTestRule
            .onNodeWithTag("loading_dialog")
            .assertIsDisplayed()
    }

}