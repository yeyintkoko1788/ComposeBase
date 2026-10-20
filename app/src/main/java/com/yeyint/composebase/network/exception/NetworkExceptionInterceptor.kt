package com.yeyint.composebase.network.exception

import com.yeyint.composebase.BuildConfig
import com.yeyint.composebase.network.showLogE
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.yeyint.composebase.domain.model.LocalizedTextVO
import com.yeyint.composebase.domain.model.MaintenanceInfo
import com.yeyint.composebase.network.components.MaintenanceEventBus
import com.yeyint.composebase.network.response.LocalizedText
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.jvm.Throws

/** What this backend sends for "under scheduled maintenance," as opposed to an ordinary server
 *  error — confirm the value with the backend team for each new project. */
private const val HTTP_MAINTENANCE = 503

private val gson = Gson()

/**
 * Title from `meta.message`, description from `body` — both localized `{en, my, zh}` objects, but
 * `body` arrives as a JSON *string* containing that object (double-encoded), so it's decoded a
 * second time; a plain object or a plain non-JSON string are tolerated too. Anything missing or
 * unparseable just stays null and the screen falls back to its built-in wording for that part.
 */
private fun parseMaintenanceInfo(response: String): MaintenanceInfo = runCatching {
    val root = JsonParser.parseString(response).asJsonObject
    MaintenanceInfo(
        title = runCatching { root.getAsJsonObject("meta")?.get("message")?.let(::localizedFrom) }.getOrNull(),
        description = runCatching { root.get("body")?.let(::localizedFrom) }.getOrNull()
    )
}.getOrDefault(MaintenanceInfo())

private fun localizedFrom(element: JsonElement): LocalizedTextVO? {
    val text = when {
        element.isJsonObject -> gson.fromJson(element, LocalizedText::class.java)
        element.isJsonPrimitive && element.asJsonPrimitive.isString -> {
            val raw = element.asString
            runCatching { gson.fromJson(raw, LocalizedText::class.java) }.getOrNull()
                ?: LocalizedText(en = raw, my = raw, zh = raw)
        }
        else -> null
    } ?: return null
    return LocalizedTextVO(en = text.en.orEmpty(), my = text.my.orEmpty(), zh = text.zh.orEmpty())
        .takeIf { it.en.isNotBlank() || it.my.isNotBlank() || it.zh.isNotBlank() }
}

class NetworkExceptionInterceptor(
    private val maintenanceEventBus: MaintenanceEventBus
) : Interceptor {

    @Throws()
    override fun intercept(chain : Interceptor.Chain) : Response {

        val response = chain.proceed(chain.request())

        return when (response.isSuccessful) {
            true -> response
            false -> {
                // peekBody(...).string() copies the whole error body into memory, so it is only read
                // when something needs it: the debug log, or the maintenance screen's text.
                val isMaintenance = response.code == HTTP_MAINTENANCE
                val peekedBody = if (BuildConfig.DEBUG || isMaintenance) {
                    response.peekBody(Long.MAX_VALUE).string()
                } else null
                if (BuildConfig.DEBUG) showLogE("NetworkError", "HTTP ${response.code}: $peekedBody")
                if (isMaintenance && peekedBody != null) {
                    // Side channel to the app shell, in addition to (not instead of) the normal
                    // NetworkException below — whichever screen made this call still gets a
                    // typed error the same way it always does, it's just moot once the shell
                    // takes over with the maintenance screen.
                    maintenanceEventBus.emitMaintenanceDetected(parseMaintenanceInfo(peekedBody))
                }
                throw NetworkException(response.body, response.code)
            }
        }
    }
}