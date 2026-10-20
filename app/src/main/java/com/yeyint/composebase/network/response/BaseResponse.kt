package com.yeyint.composebase.network.response

import com.google.gson.annotations.SerializedName
import com.yeyint.composebase.uitls.LOCALE_CN
import com.yeyint.composebase.uitls.LOCALE_MM

abstract class BaseResponse<T> {
    abstract val meta : MetaData
    abstract val body: BodyData<T>
}

data class BodyData<T>(
    val data: T?
)

data class MetaData(
     // Without this the shared Gson (LOWER_CASE_WITH_UNDERSCORES) looks for "status_code" and this
     // is silently null on every response. Found by ResponseDtoSerializedNameTest.
     @SerializedName("statusCode") val statusCode: Int?,
     val success: Boolean,
     val message: String
)

/** For APIs that send text in every language and let the client pick, rather than one string picked
 *  server-side (the server doesn't know which language the caller wants). */
data class LocalizedText(
    @SerializedName("en") val en: String? = null,
    @SerializedName("my") val my: String? = null,
    @SerializedName("zh") val zh: String? = null
)

/** [languageCode] is one of [LOCALE_MM]/[LOCALE_CN]/`LOCALE_EN` — the code the language setting
 *  stores. A missing or blank translation falls back to English, then to empty. */
fun LocalizedText?.forLanguage(languageCode: String): String {
    val text = when (languageCode) {
        LOCALE_MM -> this?.my
        LOCALE_CN -> this?.zh
        else -> this?.en
    }
    return text?.ifBlank { null } ?: this?.en.orEmpty()
}
