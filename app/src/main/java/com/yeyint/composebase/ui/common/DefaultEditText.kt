package com.yeyint.composebase.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.neutral_border_light
import com.yeyint.composebase.ui.theme.neutral_border_light_disable
import com.yeyint.composebase.ui.theme.neutral_text
import com.yeyint.composebase.ui.theme.neutral_text_disable
import com.yeyint.composebase.ui.theme.neutral_text_light
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.regularBodySmall

@Composable
fun DefaultEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isPasswordField: Boolean = false,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    showCounter: Boolean = false,
    maxLength: Int = Int.MAX_VALUE
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val labelStyle = if (isFocused || value.isNotEmpty()) {
        MaterialTheme.typography.regularBodySmall
    } else {
        MaterialTheme.typography.regularBodyMedium
    }

    val finalVisualTransformation = when {
        isPasswordField && !passwordVisible -> PasswordVisualTransformation()
        isPasswordField && passwordVisible -> VisualTransformation.None
        else -> visualTransformation
    }

    val finalKeyboardOptions = if (keyboardOptions.keyboardType != KeyboardOptions.Default.keyboardType) {
        // A caller-specified keyboard type (e.g. NumberPassword) always wins.
        keyboardOptions
    } else if (isPasswordField) {
        KeyboardOptions(keyboardType = KeyboardType.Password)
    } else {
        keyboardOptions
    }

    val finalTrailingIcon: @Composable (() -> Unit)? = if (isPasswordField) {
        {
            val image = if (passwordVisible) {
                R.drawable.ic_hide_password
            } else {
                R.drawable.ic_show_password
            }
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = description,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    } else {
        trailingIcon
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                val sanitized = if (singleLine) {
                    // Strip newlines — some IMEs inject \n on Enter even for single-line fields.
                    newValue.replace("\n", "")
                } else {
                    newValue
                }
                if (sanitized.length <= maxLength) {
                    onValueChange(sanitized)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource,
            label = {
                Text(
                    text = label,
                    style = labelStyle,
                    color = neutral_text_light
                )
            },
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.regularBodyMedium,
                        color = neutral_text_light
                    )
                }
            },
            supportingText = if (showCounter && !(isError && errorMessage != null)) {
                {
                    Text(
                        text = "${value.length} / $maxLength",
                        style = MaterialTheme.typography.regularBodySmall,
                        color = neutral_text_light,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            } else null,
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = finalTrailingIcon,
            keyboardOptions = finalKeyboardOptions,
            visualTransformation = finalVisualTransformation,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = neutral_border_light,
                disabledBorderColor = neutral_border_light_disable,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = neutral_text,
                unfocusedLabelColor = neutral_text,
                disabledLabelColor = neutral_text_disable,
                errorLabelColor = neutral_text,
                focusedTextColor = neutral_text,
                unfocusedTextColor = neutral_text,
                disabledTextColor = neutral_text_disable,
            )
        )
        if (isError && errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error_alert),
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultEditTextPreview() {
    BasePreviewWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            var text1 by remember { mutableStateOf("") }
            DefaultEditText(
                value = text1,
                onValueChange = { text1 = it },
                label = "Employee ID / Phone"
            )
            Spacer(modifier = Modifier.height(24.dp))

            var text2 by remember { mutableStateOf("EMP1024") }
            DefaultEditText(
                value = text2,
                onValueChange = { text2 = it },
                label = "Employee ID / Phone"
            )
            Spacer(modifier = Modifier.height(24.dp))

            var text3 by remember { mutableStateOf("EdP1024") }
            DefaultEditText(
                value = text3,
                onValueChange = { text3 = it },
                label = "Employee ID / Phone",
                isError = true,
                errorMessage = "Not found"
            )
            Spacer(modifier = Modifier.height(24.dp))

            var text4 by remember { mutableStateOf("EMP1024") }
            DefaultEditText(
                value = text4,
                onValueChange = { text4 = it },
                label = "Employee ID / Phone",
                enabled = false
            )
            Spacer(modifier = Modifier.height(24.dp))

            var password by remember { mutableStateOf("password") }
            DefaultEditText(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPasswordField = true
            )
        }
    }
}
