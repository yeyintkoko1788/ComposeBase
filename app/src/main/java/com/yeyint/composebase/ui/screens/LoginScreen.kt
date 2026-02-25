package com.yeyint.composebase.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yeyint.composebase.R
import com.yeyint.composebase.isNotEmptyString
import com.yeyint.composebase.ui.common.DefaultEditText
import com.yeyint.composebase.ui.common.LoadingDialog
import com.yeyint.composebase.ui.common.PrimaryButton
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.regularBodySmall
import com.yeyint.composebase.ui.theme.semiboldHeading4
import com.yeyint.composebase.ui.viewmodel.login.LoginContract
import com.yeyint.composebase.ui.viewmodel.login.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LoginScreen(
    contract: LoginContract,
    navigateToHome: () -> Unit,
    onTermsClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onLanguageChangeClicked: () -> Unit = {}
) {
    var employeeId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(true) }
    val loginUiState by contract.loginUiState.collectAsStateWithLifecycle()

    LaunchedEffect(loginUiState) {
        if (loginUiState is LoginUiState.Success) {
            navigateToHome()
        }
    }
    when (loginUiState) {

        is LoginUiState.Loading -> {
            LoadingDialog(
                modifier = Modifier.zIndex(999f)
            )
        }

        is LoginUiState.Error -> {
            // show error UI
        }

        else -> Unit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_english),
            contentDescription = "Change language",
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = { onLanguageChangeClicked() })
                .align(Alignment.End)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Base App",
                    fontSize = 36.sp,
                    style = MaterialTheme.typography.semiboldHeading4.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4E13C7),
                                Color(0xFF5625B9),
                                Color(0xFF40247A)
                            )
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Login now and get started",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.regularBodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            DefaultEditText(
                value = employeeId,
                onValueChange = { employeeId = it },
                label = stringResource(R.string.txt_employee_id_phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DefaultEditText(
                value = pin,
                onValueChange = { pin = it },
                label = stringResource(R.string.txt_6_digit_pin),
                isPasswordField = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = { agreeToTerms = it },
                )
                val annotatedString = buildAnnotatedString {
                    append(stringResource(R.string.txt_i_agree_to_attendio_s))
                    pushStringAnnotation(tag = "TERMS", annotation = "terms")
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(stringResource(R.string.txt_terms))
                    }
                    pop()
                    append(stringResource(R.string.txt_and))
                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(stringResource(R.string.txt_privacy_policy))
                    }
                    pop()
                    append(".")
                }
                ClickableText(
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(
                            tag = "TERMS",
                            start = offset,
                            end = offset
                        )
                            .firstOrNull()?.let {
                                onTermsClicked()
                            }
                        annotatedString.getStringAnnotations(
                            tag = "PRIVACY",
                            start = offset,
                            end = offset
                        )
                            .firstOrNull()?.let {
                                onPrivacyPolicyClicked()
                            }
                    },
                    style = MaterialTheme.typography.regularBodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = stringResource(R.string.txt_login),
                onClick = {
                    contract.login(employeeId, pin)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = agreeToTerms && employeeId.isNotEmptyString() && pin.isNotEmptyString()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

class FakeLoginContract(
    initialState: LoginUiState = LoginUiState.Idle
) : LoginContract {
    override val loginUiState: StateFlow<LoginUiState> = MutableStateFlow(initialState)
    override fun login(email: String, password: String) {}
}

@Preview
@Composable
private fun DefaultPreview() {
    BasePreviewWrapper {
        LoginScreen(
            contract = FakeLoginContract(),
            navigateToHome = {},
            onTermsClicked = {},
            onPrivacyPolicyClicked = {}
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun LoginScreenLoadingPreview() {
    BasePreviewWrapper {
        LoginScreen(
            contract = FakeLoginContract(initialState = LoginUiState.Loading),
            navigateToHome = {},
            onTermsClicked = {},
            onPrivacyPolicyClicked = {}
        )
    }
}
