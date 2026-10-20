package com.yeyint.composebase.domain.model

/**
 * The backend reported maintenance. [title] is the response's own `meta.message`; [description] is
 * its `body`, shown as HTML (a plain-text body just has no tags). Either is null when the response
 * didn't carry a usable one — the maintenance screen then uses its built-in wording for that part.
 */
data class MaintenanceInfo(
    val title: LocalizedTextVO? = null,
    val description: LocalizedTextVO? = null
)
