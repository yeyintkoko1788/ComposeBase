package com.yeyint.composebase.domain.model

import com.yeyint.composebase.uitls.LOCALE_CN
import com.yeyint.composebase.uitls.LOCALE_MM

/** Domain-side counterpart of [com.yeyint.composebase.network.response.LocalizedText] — kept
 *  separate rather than reused so domain models never depend on a `network.response` type. */
data class LocalizedTextVO(val en: String, val my: String, val zh: String) {
    fun forLanguage(languageCode: String): String {
        val text = when (languageCode) {
            LOCALE_MM -> my
            LOCALE_CN -> zh
            else -> en
        }
        return text.ifBlank { en }
    }
}
