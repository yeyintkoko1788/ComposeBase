package com.yeyint.composebase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.neutral_border_light
import com.yeyint.composebase.ui.theme.neutral_border_light_disable
import com.yeyint.composebase.ui.theme.neutral_text
import com.yeyint.composebase.ui.theme.neutral_text_disable
import com.yeyint.composebase.ui.theme.neutral_text_light
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.regularBodySmall

/**
 * An alternate text-field style with a floating outside-the-border label, used where
 * [DefaultEditText]'s Material OutlinedTextField look isn't the right fit (e.g. dense forms).
 */
@Composable
fun CustomizeEditText(
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
    maxLength: Int = Int.MAX_VALUE,
    minHeight: Dp = Dp.Unspecified
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val finalVisualTransformation =
        if (isPasswordField && !passwordVisible) PasswordVisualTransformation()
        else visualTransformation

    val finalKeyboardOptions =
        if (isPasswordField) KeyboardOptions(keyboardType = KeyboardType.Password)
        else keyboardOptions

    val finalTrailingIcon: @Composable (() -> Unit)? = if (isPasswordField) {
        {
            val iconRes =
                if (passwordVisible) R.drawable.ic_hide_password
                else R.drawable.ic_show_password

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    } else trailingIcon

    val borderColor =
        if (isError) MaterialTheme.colorScheme.error else neutral_border_light

    Column(modifier = modifier.fillMaxWidth()) {

        Box(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .then(
                        if (minHeight != Dp.Unspecified)
                            Modifier.heightIn(min = minHeight)
                        else Modifier
                    )
                    .border(
                        1.dp,
                        if (enabled) borderColor else neutral_border_light_disable,
                        RoundedCornerShape(16.dp)
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            // prevent overlap with counter
                            .padding(bottom = if (showCounter) 20.dp else 0.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            BasicTextField(
                                value = value,
                                onValueChange = {
                                    if (it.length <= maxLength) onValueChange(it)
                                },
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography
                                    .regularBodyMedium.copy(
                                        color = if (enabled) neutral_text else neutral_text_disable
                                    ),
                                keyboardOptions = finalKeyboardOptions,
                                visualTransformation = finalVisualTransformation,
                                singleLine = singleLine,
                                minLines = minLines,
                                maxLines = maxLines,
                                enabled = enabled,
                                readOnly = readOnly,
                                decorationBox = { inner ->
                                    Box {
                                        if (value.isEmpty()) {
                                            Text(
                                                text = placeholder ?: "",
                                                style = MaterialTheme.typography.regularBodyMedium,
                                                color = neutral_text_light
                                            )
                                        }
                                        inner()
                                    }
                                }
                            )
                            if (finalTrailingIcon != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                finalTrailingIcon()
                            }
                        }
                    }
                    if (showCounter) {
                        Text(
                            text = "${value.length} / $maxLength",
                            style = MaterialTheme.typography.regularBodySmall,
                            color = neutral_text_light,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 2.dp, bottom = 2.dp)
                        )
                    }
                }
            }

            // Floating label
            Text(
                text = " $label ",
                style = MaterialTheme.typography.regularBodySmall,
                color = if (enabled) neutral_text_light else neutral_text_disable,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .align(Alignment.TopStart)
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        if (isError && errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_error_alert),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.regularBodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomizeEditTextPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomizeEditText(
                value = "",
                onValueChange = {},
                label = "Full Name",
                placeholder = "Enter your name"
            )
            CustomizeEditText(
                value = "John Doe",
                onValueChange = {},
                label = "Full Name"
            )
            CustomizeEditText(
                value = "abc",
                onValueChange = {},
                label = "Username",
                isError = true,
                errorMessage = "Username is invalid"
            )
            CustomizeEditText(
                value = "password123",
                onValueChange = {},
                label = "Password",
                isPasswordField = true
            )
        }
    }
}
