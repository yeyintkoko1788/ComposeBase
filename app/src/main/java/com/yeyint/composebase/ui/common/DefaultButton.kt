package com.yeyint.composebase.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.brand_primary_disable
import com.yeyint.composebase.ui.theme.mediumBodyLarge
import com.yeyint.composebase.ui.theme.n011
import com.yeyint.composebase.ui.theme.neutral_border_dark
import com.yeyint.composebase.ui.theme.neutral_border_dark_disable
import com.yeyint.composebase.ui.theme.neutral_text
import com.yeyint.composebase.ui.theme.neutral_text_disable
import com.yeyint.composebase.ui.theme.neutral_text_invert
import com.yeyint.composebase.ui.theme.neutral_text_invert_disable

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled : Boolean = true,
    buttonColors : Color = MaterialTheme.colorScheme.primary ,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColors,
            disabledContentColor = neutral_text_invert_disable,
            disabledContainerColor = brand_primary_disable
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.mediumBodyLarge,
            color = when (enabled) {
                true -> neutral_text_invert
                false -> neutral_text_invert_disable
            }
        )
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled : Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = when (enabled) {
                    true -> neutral_border_dark
                    false -> neutral_border_dark_disable
                },
                shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = n011,
            disabledContentColor = neutral_text_disable,
            disabledContainerColor = n011
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.mediumBodyLarge,
            color = when (enabled) {
                true -> neutral_text
                false -> neutral_text_disable
            }
        )
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    BasePreviewWrapper {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Primary Button" ,
                style = MaterialTheme.typography.mediumBodyLarge
            )
            PrimaryButton(text = "Button", onClick = {})
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Disabled Primary Button" ,
                style = MaterialTheme.typography.mediumBodyLarge
            )
            PrimaryButton(text = "Button", enabled = false, onClick = {})
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Secondary Button" ,
                style = MaterialTheme.typography.mediumBodyLarge
            )
            SecondaryButton(text = "Button", onClick = {})
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Disabled Secondary Button" ,
                style = MaterialTheme.typography.mediumBodyLarge
            )
            SecondaryButton(text = "Button", enabled = false, onClick = {})
        }
    }
}