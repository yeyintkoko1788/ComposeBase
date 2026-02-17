package com.yeyint.composebase.ui.common

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.neutral_border_dark
import com.yeyint.composebase.ui.theme.neutral_border_dark_disable
import com.yeyint.composebase.ui.theme.neutral_text
import com.yeyint.composebase.ui.theme.neutral_text_disable

@Composable
fun DefaultEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = label) },
            isError = isError,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = neutral_border_dark,
                disabledBorderColor = neutral_border_dark_disable,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = neutral_text,
                disabledLabelColor = neutral_text_disable,
                errorLabelColor = MaterialTheme.colorScheme.error,
                focusedTextColor = neutral_text,
                unfocusedTextColor = neutral_text,
                disabledTextColor = neutral_text_disable,
            )
        )
        if (isError && errorMessage != null) {
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
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
        }
    }
}