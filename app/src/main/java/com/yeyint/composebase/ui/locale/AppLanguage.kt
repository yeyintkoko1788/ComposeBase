package com.yeyint.composebase.ui.locale

import com.yeyint.composebase.network.logSwallowed
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.yeyint.composebase.network.components.MyPreference
import com.yeyint.composebase.uitls.LOCALE_EN
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Selected app language, persisted in [MyPreference] under the `LOCALE` key.
 *
 * Deliberately independent of the device language: a user may want the app in Burmese on an
 * English phone. There is no "follow system" mode — every install resolves to an explicit code,
 * [LOCALE_EN] until the user picks something else.
 */
@Singleton
class LanguageManager @Inject constructor(
    private val preference: MyPreference
) {
    private val _language = MutableStateFlow(
        preference.load(MyPreference.KEYS.LOCALE)?.takeIf { it.isNotBlank() } ?: LOCALE_EN
    )
    val language: StateFlow<String> = _language.asStateFlow()

    fun setLanguage(code: String) {
        if (_language.value == code) return
        preference.save(MyPreference.KEYS.LOCALE, code)
        _language.value = code
    }
}

/**
 * Applies [languageCode] to a [Context], returning a wrapper that resolves resources in that
 * locale.
 *
 * Returning a [ContextWrapper] around the receiver — rather than `createConfigurationContext`'s
 * result directly — matters. That method returns a bare `ContextImpl` with no link back to the
 * Activity, and several APIs walk the `ContextWrapper.baseContext` chain looking for one.
 * `hiltViewModel()` is the notable case: it needs the Activity to build a `HiltViewModelFactory`
 * and throws "Expected an activity context" if it can't find it. Wrapping keeps that chain intact
 * while still overriding resource lookups.
 */
fun Context.withAppLanguage(languageCode: String): Context {
    // `-u-nu-latn` forces plain Arabic-numeral digits (0-9) regardless of language. Without it,
    // Burmese renders %d as native digits (e.g. "5" -> "၅") in every stringResource(id, count)
    // call and every SimpleDateFormat-based label — none of which designs or backends expect
    // localized. This is the one place that decides the app's locale, so it's the one place this
    // needs fixing rather than patching each call site.
    val locale = Locale.forLanguageTag("$languageCode-u-nu-latn")
    // Keeps SimpleDateFormat and friends in step with the chosen language.
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration).apply {
        setLocale(locale)
        setLayoutDirection(locale)
    }
    val localized = createConfigurationContext(config)
    return object : ContextWrapper(this) {
        override fun getResources(): Resources = localized.resources
        override fun getAssets(): AssetManager = localized.assets
    }
}

/**
 * Reads the saved language without Hilt.
 *
 * `attachBaseContext` runs before field injection, so [LanguageManager] isn't available yet at the
 * point the activity needs to know which locale to start in. [MyPreference] is a plain class, so
 * it can be constructed directly here.
 */
fun readSavedLanguage(context: Context): String =
    runCatching {
        MyPreference(context).load(MyPreference.KEYS.LOCALE)?.takeIf { it.isNotBlank() }
    }.onFailure { logSwallowed("readSavedLanguage: falling back to $LOCALE_EN", it) }.getOrNull() ?: LOCALE_EN

/** Current language plus the setter, so any composable can read or change it without plumbing. */
@Immutable
data class LanguageController(
    val code: String,
    val onSelect: (String) -> Unit
)

val LocalLanguage: ProvidableCompositionLocal<LanguageController> = compositionLocalOf {
    error("LocalLanguage not provided — wrap the app in AppLanguageProvider()")
}

/**
 * Re-scopes [LocalContext] and [LocalConfiguration] to the selected locale, so `stringResource`
 * resolves against it.
 *
 * This is why switching is instant: changing [languageCode] produces a new context, every
 * `stringResource` reader recomposes, and no `Activity.recreate()` (with its flash of a rebuilt
 * screen and lost scroll position) is needed.
 *
 * The provided context must stay unwrappable to the Activity — see [withAppLanguage].
 */
@Composable
fun AppLanguageProvider(
    languageCode: String,
    onSelect: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val localizedContext = remember(languageCode, context) {
        context.withAppLanguage(languageCode)
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration,
        LocalLanguage provides LanguageController(languageCode, onSelect),
        content = content
    )
}

/**
 * Re-applies the selected language inside a `Popup` or `Dialog`.
 *
 * **Both host their content in a new `AndroidComposeView`**, and every composition root runs
 * `ProvideAndroidCompositionLocals`, which provides `LocalContext` and `LocalConfiguration` from
 * *that view's own* context — the Activity. So [AppLanguageProvider]'s override, applied once
 * around the app, is silently discarded the moment content moves into an overlay window, and
 * `stringResource` there resolves against the language the Activity was created with.
 *
 * [LocalLanguage] survives, because it is not one of the locals Compose re-provides, so the
 * current selection can be read back here and the scope rebuilt.
 *
 * Wrap the content of every `Popup`, `Dialog` and `ModalBottomSheet` in this. Missing one is easy
 * to overlook: the symptom only appears for strings that actually differ between locales.
 */
@Composable
fun AppLanguageScope(content: @Composable () -> Unit) {
    val language = LocalLanguage.current
    AppLanguageProvider(
        languageCode = language.code,
        onSelect = language.onSelect,
        content = content
    )
}
