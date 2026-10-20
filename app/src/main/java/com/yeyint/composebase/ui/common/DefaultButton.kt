package com.yeyint.composebase.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.locale.LocalLanguage
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.surface_brand
import com.yeyint.composebase.ui.theme.disabled_button
import com.yeyint.composebase.ui.theme.surface_error
import com.yeyint.composebase.ui.theme.error_disable
import com.yeyint.composebase.ui.theme.mediumBodyLarge
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.stroke_lighter
import com.yeyint.composebase.ui.theme.neutral_border_dark_disable
import com.yeyint.composebase.ui.theme.text_default
import com.yeyint.composebase.ui.theme.text_light
import com.yeyint.composebase.ui.theme.text_inverted
import com.yeyint.composebase.uitls.LOCALE_MM
import com.yeyint.composebase.ui.theme.neutral_text_invert_disable
import com.yeyint.composebase.ui.theme.semiboldBodyMedium

/**
 * Label style shared by every button here. Two button-only adjustments on top of the theme style:
 * - No fixed line height — the theme styles carry one sized for body text, which adds spacing a
 *   single-line button label doesn't want (and lets the font's own metrics decide instead).
 * - 2sp smaller in Myanmar, on top of the global trim [appTypography] already applies to every
 *   style there: Myanmar glyphs run large, and a button has far less room to absorb that than
 *   body text does.
 */
@Composable
private fun TextStyle.asButtonLabel(): TextStyle {
    val isMyanmar = LocalLanguage.current.code == LOCALE_MM
    return copy(
        fontSize = if (isMyanmar) (fontSize.value - 2f).sp else fontSize,
        lineHeight = TextUnit.Unspecified
    )
}

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
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColors,
            disabledContentColor = neutral_text_invert_disable,
            disabledContainerColor = disabled_button
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.semiboldBodyMedium.asButtonLabel(),
            color = when (enabled) {
                true -> text_inverted
                false -> neutral_text_invert_disable
            }
        )
    }
}

/**
 * A primary-styled button that swaps its label for a spinner + [loadingText] while [isLoading]
 * is true, and disables itself so it can't be tapped twice while a request is in flight.
 */
@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    text: String,
    loadingText: String = stringResource(R.string.action_loading),
    isLoading: Boolean = false,
    enabled: Boolean = true,
    iconRes: Int? = null,
    onClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = contentPadding,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = surface_brand,
            disabledContainerColor = disabled_button,
            contentColor = text_inverted,
            disabledContentColor = neutral_text_invert_disable
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = text_inverted,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = loadingText,
                    style = MaterialTheme.typography.semiboldBodyMedium.asButtonLabel(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = neutral_text_invert_disable
                )
            } else {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled) text_inverted else neutral_text_invert_disable
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.mediumBodyLarge.asButtonLabel(),
                    color = if (enabled) text_inverted else neutral_text_invert_disable
                )
            }
        }
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
                    true -> stroke_lighter
                    false -> neutral_border_dark_disable
                },
                shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = surface_default,
            disabledContentColor = text_light,
            disabledContainerColor = surface_default
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.semiboldBodyMedium.asButtonLabel(),
            color = when (enabled) {
                true -> text_default
                false -> text_light
            }
        )
    }
}

/**
 * Solid red rather than the brand color — for actions like "Delete Account" that shouldn't read
 * as encouraged. [isLoading] swaps the label for a spinner + [loadingText], same as [LoadingButton].
 */
@Composable
fun DestructiveButton(
    modifier: Modifier = Modifier,
    text: String,
    loadingText: String = stringResource(R.string.action_loading),
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = surface_error,
            disabledContainerColor = error_disable,
            contentColor = text_inverted,
            disabledContentColor = neutral_text_invert_disable
        )
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = text_inverted,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = loadingText,
                    style = MaterialTheme.typography.mediumBodyLarge.asButtonLabel(),
                    color = neutral_text_invert_disable
                )
            }
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.mediumBodyLarge.asButtonLabel(),
                color = if (enabled) text_inverted else neutral_text_invert_disable
            )
        }
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