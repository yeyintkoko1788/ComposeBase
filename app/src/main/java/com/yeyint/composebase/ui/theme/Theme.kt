package com.yeyint.composebase.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.yeyint.composebase.ui.locale.LanguageController
import com.yeyint.composebase.ui.locale.LocalLanguage
import com.yeyint.composebase.uitls.LOCALE_EN

private val DarkColorScheme = darkColorScheme(
    primary = brand_primary_default,
    secondary = brand_secondary_default,
    tertiary = Pink80,
    error = semantic_error_default
)

private val LightColorScheme = lightColorScheme(
    primary = brand_primary_default,
    secondary = brand_secondary_default,
    tertiary = Pink80,
    error = semantic_error_default

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ComposeBaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun BaseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography ,
        content = content
    )
}

@Composable
fun BasePreviewWrapper(
    content: @Composable BoxWithConstraintsScope.() -> Unit,
) {
    // LocalLanguage is provided OUTSIDE BaseTheme on purpose: anything in the theme that reads it
    // (e.g. a language-dependent typography scale) is evaluated as an argument to MaterialTheme,
    // one level above BaseTheme's content lambda — nested inside, it would run with nothing
    // provided and every preview would crash with "LocalLanguage not provided". The real app
    // never hits this because MainActivity wraps everything in AppLanguageProvider first.
    CompositionLocalProvider(LocalLanguage provides LanguageController(LOCALE_EN) {}) {
        BaseTheme {
            Surface {
                BoxWithConstraints {
                    content()
                }
            }
        }
    }
}
