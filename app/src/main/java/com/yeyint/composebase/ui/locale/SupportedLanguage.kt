package com.yeyint.composebase.ui.locale

import androidx.annotation.StringRes
import com.yeyint.composebase.R
import com.yeyint.composebase.uitls.LOCALE_CN
import com.yeyint.composebase.uitls.LOCALE_EN
import com.yeyint.composebase.uitls.LOCALE_MM

/**
 * The app's supported languages — hardcoded, not fetched. The set is fixed by the `values-*`
 * resource folders that actually exist, so a server list could only ever disagree with it.
 *
 * To add a language: add an entry here, a `language_<code>` name string in `values/strings.xml`
 * (`translatable="false"` — a language is always shown in its own script), and a
 * `values-<code>/strings.xml` with every translatable key. See docs/LOCALIZATION.md.
 */
enum class SupportedLanguage(
    val localCode: String,
    @StringRes val labelRes: Int
) {
    ENGLISH(LOCALE_EN, R.string.language_en),
    MYANMAR(LOCALE_MM, R.string.language_my),
    CHINESE(LOCALE_CN, R.string.language_zh);

    companion object {
        val default: SupportedLanguage = ENGLISH

        fun fromCode(code: String): SupportedLanguage =
            entries.firstOrNull { it.localCode == code } ?: default
    }
}
