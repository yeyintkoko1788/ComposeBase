package com.yeyint.composebase.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yeyint.composebase.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * "Today"/"Yesterday"/"12 Aug 2026" — shared by History and Notification's date-group headers.
 * Resolved here, at render time, from a stored [LocalDate] rather than baked into a stored label
 * string — see CLAUDE.md's "store types, not rendered labels" rule. "Today"/"Yesterday" ARE
 * translated (they're ordinary UI vocabulary); the day/month/year fallback is deliberately NOT —
 * dates, times and numbers stay in English throughout this app (same as
 * formatAmount/formatPrice/formatDistance's `Locale.US`), so a date outside those two days always
 * reads e.g. "12 Aug 2026" regardless of app language.
 */
@Composable
fun relativeDateLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> stringResource(R.string.date_today)
        today.minusDays(1) -> stringResource(R.string.date_yesterday)
        else -> DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US).format(date)
    }
}
