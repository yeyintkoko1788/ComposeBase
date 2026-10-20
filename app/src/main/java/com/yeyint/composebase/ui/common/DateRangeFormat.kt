package com.yeyint.composebase.ui.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Shared by [ReportViewModel][com.yeyint.composebase.ui.viewmodel.report.ReportViewModel]'s
 * custom-range label and [HistoryViewModel][com.yeyint.composebase.ui.viewmodel.history.HistoryViewModel]'s
 * date filter chip — both read [DateRangePickerDialog]'s [Long] millis the same way, so the same
 * "same day → just one date" collapse belongs in one place rather than two copies that could drift.
 */
fun formatDateRangeLabel(startMillis: Long, endMillis: Long): String {
    val start = Calendar.getInstance().apply { timeInMillis = startMillis }
    val end = Calendar.getInstance().apply { timeInMillis = endMillis }
    val sameDay = start.get(Calendar.YEAR) == end.get(Calendar.YEAR) &&
        start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR)
    // Every pattern here includes "MMM" — kept in English regardless of app language, same
    // reasoning as clockTimeLabel's own doc comment, so dates stay recognisable rather than
    // switching script per locale.
    if (sameDay) {
        return SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(start.time)
    }
    val sameYear = start.get(Calendar.YEAR) == end.get(Calendar.YEAR)
    val startPattern = if (sameYear) "d MMM" else "d MMM yyyy"
    val startLabel = SimpleDateFormat(startPattern, Locale.ENGLISH).format(start.time)
    val endLabel = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(end.time)
    return "$startLabel – $endLabel"
}
