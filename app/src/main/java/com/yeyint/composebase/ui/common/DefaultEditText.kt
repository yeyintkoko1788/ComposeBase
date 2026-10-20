package com.yeyint.composebase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.mediumBodySmall
import com.yeyint.composebase.ui.theme.neutral_surface
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.regularBodySmall
import com.yeyint.composebase.ui.theme.stroke_error
import com.yeyint.composebase.ui.theme.stroke_lighter
import com.yeyint.composebase.ui.theme.surface_brand
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.text_default
import com.yeyint.composebase.ui.theme.text_error
import com.yeyint.composebase.ui.theme.text_light


@Composable
fun DefaultEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    required: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isPasswordField: Boolean = false,
    // Tells autofill services what the field takes, so saved credentials are offered and a
    // "save password?" prompt can appear. A password field defaults to [ContentType.Password].
    autofillType: ContentType? = null,
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

    val borderColor = when {
        isError -> stroke_error
        isFocused -> stroke_lighter
        else -> stroke_lighter
    }

    val finalVisualTransformation = when {
        isPasswordField && !passwordVisible -> PasswordVisualTransformation()
        isPasswordField && passwordVisible -> VisualTransformation.None
        else -> visualTransformation
    }

    val finalKeyboardOptions = when {
        // A caller-specified keyboard type (e.g. NumberPassword) always wins.
        keyboardOptions.keyboardType != KeyboardOptions.Default.keyboardType -> keyboardOptions
        isPasswordField -> KeyboardOptions(keyboardType = KeyboardType.Password)
        else -> keyboardOptions
    }

    Column(modifier = modifier) {
        if (label != null) {
            Row {
                Text(
                    text = label,
                    style = MaterialTheme.typography.regularBodySmall,
                    color = text_default
                )
                if (required) {
                    Text(
                        text = " *",
                        style = MaterialTheme.typography.regularBodySmall,
                        color = text_error
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FIELD_HEIGHT)
                .clip(RoundedCornerShape(FIELD_RADIUS))
                .background(if (enabled) surface_default else neutral_surface)
                .border(1.dp, borderColor, RoundedCornerShape(FIELD_RADIUS))
                .padding(
                    horizontal = 16.dp,
                    vertical = if (singleLine) 0.dp else 12.dp
                ),
            verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.regularBodyMedium,
                        color = stroke_lighter
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        val sanitized = if (singleLine) {
                            // Strip newlines — some IMEs inject \n on Enter even for single-line
                            // fields, which would otherwise grow the box.
                            newValue.replace("\n", "")
                        } else {
                            newValue
                        }
                        if (sanitized.length <= maxLength) onValueChange(sanitized)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            (autofillType ?: ContentType.Password.takeIf { isPasswordField })
                                ?.let { type -> Modifier.semantics { contentType = type } }
                                ?: Modifier
                        ),
                    enabled = enabled,
                    readOnly = readOnly,
                    interactionSource = interactionSource,
                    textStyle = MaterialTheme.typography.regularBodyMedium
                        .copy(color = text_default),
                    cursorBrush = SolidColor(surface_brand),
                    keyboardOptions = finalKeyboardOptions,
                    visualTransformation = finalVisualTransformation,
                    singleLine = singleLine,
                    minLines = minLines,
                    maxLines = maxLines
                )
            }

            when {
                isPasswordField -> {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
                        ),
                        contentDescription = if (passwordVisible) {
                            stringResource(R.string.cd_hide_password)
                        } else {
                            stringResource(R.string.cd_show_password)
                        },
                        tint = text_light,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { passwordVisible = !passwordVisible }
                    )
                }

                trailingIcon != null -> {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }

        if (isError && errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error_alert),
                    contentDescription = stringResource(R.string.cd_error),
                    tint = text_error,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = errorMessage,
                    color = text_error,
                    style = MaterialTheme.typography.regularBodySmall
                )
            }
        } else if (showCounter) {
            Text(
                text = "${value.length} / $maxLength",
                style = MaterialTheme.typography.regularBodySmall,
                color = text_light,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}

private val FIELD_HEIGHT = 52.dp
private val FIELD_RADIUS = 8.dp

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun DefaultEditTextPreview() {
    BasePreviewWrapper {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DefaultEditText(
                value = "",
                onValueChange = {},
                label = "Login ID",
                placeholder = "Enter Login ID",
                required = true,
                trailingIcon = {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_cancel
                            ),
                            contentDescription = stringResource(R.string.cd_clear_text),
                            tint = text_light,
                            modifier = Modifier
                                .clickable(enabled = true, onClick = {

                                })
                        )
                }
            )
            DefaultEditText(
                value = "RID0105",
                onValueChange = {},
                label = "Login ID",
                placeholder = "Enter Login ID",
                required = true
            )
            DefaultEditText(
                value = "supersecret",
                onValueChange = {},
                label = "Password",
                placeholder = "Enter password",
                required = true,
                isPasswordField = true
            )
            DefaultEditText(
                value = "nope",
                onValueChange = {},
                label = "Password",
                placeholder = "Enter password",
                required = true,
                isPasswordField = true,
                isError = true,
                errorMessage = "Incorrect Login ID or password"
            )
            DefaultEditText(
                value = "",
                onValueChange = {},
                label = "Notes",
                placeholder = "Enter notes",
                singleLine = false,
                minLines = 3
            )
            DefaultEditText(
                value = "This is a multiline\ntext field with\nsome content",
                onValueChange = {},
                label = "Description",
                placeholder = "Enter description",
                singleLine = false,
                minLines = 3,
                showCounter = true,
                maxLength = 200
            )
        }
    }
}
