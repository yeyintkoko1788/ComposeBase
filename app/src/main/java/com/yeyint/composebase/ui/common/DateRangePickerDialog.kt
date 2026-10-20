package com.yeyint.composebase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yeyint.composebase.R
import com.yeyint.composebase.ui.locale.AppLanguageScope
import com.yeyint.composebase.ui.theme.boldBodyXLarge
import com.yeyint.composebase.ui.theme.regularBodyMedium
import com.yeyint.composebase.ui.theme.regularBodyXLarge
import com.yeyint.composebase.ui.theme.semiboldBodyLarge
import com.yeyint.composebase.ui.theme.semiboldBodyMedium
import com.yeyint.composebase.ui.theme.semiboldBodySmall
import com.yeyint.composebase.ui.theme.surface_bg
import com.yeyint.composebase.ui.theme.surface_brand
import com.yeyint.composebase.ui.theme.surface_default
import com.yeyint.composebase.ui.theme.text_default
import com.yeyint.composebase.ui.theme.text_error
import com.yeyint.composebase.ui.theme.text_inverted
import com.yeyint.composebase.ui.theme.text_light
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

// Everything the calendar draws — month names, the weekday letters, day numbers, the picked range —
// is English with Latin digits whatever the app language, by request. Only the sheet's own labels
// (title, Start/End placeholders, Clear/Apply/Cancel) go through string resources and follow it.
private val MonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
private val DayFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)
private val DayWithYearFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
private val WeekdayLetters = listOf("S", "M", "T", "W", "T", "F", "S")

// How far back the month dropdown goes — five years is well past any report or history a rider has.
private const val MonthsBack = 60L

// Sampled from the design (428dp-wide frame at 4x).
private val PickerDividerColor = Color(0xFFE3E3E3)
private val WeekdayBandColor = Color(0xFFF7F7F7)
private val FutureDayColor = Color(0xFFB8BBC2)
private val CellHeight = 40.dp

/**
 * The History date filter and the Report custom range, as a bottom sheet drawn here rather than the
 * platform's Material date-range picker — that one's header, paging and chrome couldn't be made to
 * match the design or to localize only its labels. Same contract as before: [onConfirm] gets the
 * start and end day as UTC-midnight millis, which is what both callers already convert from, and
 * tapping the same day twice sets start and end to it ([formatDateRangeLabel] collapses that to a
 * single date).
 *
 * Every day after today is greyed out and can't be picked — neither a report nor a history filter
 * can cover days that haven't happened yet — and the month navigation stops at the current month.
 * "Today" is the rider's local date, not UTC's.
 *
 * Picking: the first tap sets the start; a later day sets the end; an earlier day moves the start
 * instead; and a tap once a full range exists begins a new one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(onConfirm: (Long, Long) -> Unit, onDismiss: () -> Unit) {
    val today = remember { LocalDate.now() }
    val thisMonth = remember { YearMonth.from(today) }
    var start by remember { mutableStateOf<LocalDate?>(null) }
    var end by remember { mutableStateOf<LocalDate?>(null) }
    var visibleMonth by remember { mutableStateOf(thisMonth) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = surface_default
    ) {
        // ModalBottomSheet composes in its own window, which re-provides LocalContext from the
        // Activity — see AppLanguageScope's doc comment.
        AppLanguageScope {
            val selectedStart = start
            val selectedEnd = end
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = stringResource(R.string.date_range_picker_title),
                    style = MaterialTheme.typography.semiboldBodyLarge,
                    color = text_default,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                RangeSummaryRow(
                    start = selectedStart,
                    end = selectedEnd,
                    today = today,
                    onClear = {
                        start = null
                        end = null
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp),
                    color = PickerDividerColor
                )
                MonthHeader(
                    visibleMonth = visibleMonth,
                    thisMonth = thisMonth,
                    onMonthSelected = { visibleMonth = it }
                )
                WeekdayRow()
                MonthGrid(
                    month = visibleMonth,
                    today = today,
                    start = selectedStart,
                    end = selectedEnd,
                    onDayClicked = { date ->
                        when {
                            selectedStart == null || selectedEnd != null -> {
                                start = date
                                end = null
                            }
                            date.isBefore(selectedStart) -> start = date
                            else -> end = date
                        }
                    }
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        text = stringResource(R.string.action_apply),
                        enabled = selectedStart != null && selectedEnd != null,
                        onClick = {
                            if (selectedStart != null && selectedEnd != null) {
                                onConfirm(selectedStart.toUtcMillis(), selectedEnd.toUtcMillis())
                            }
                        }
                    )
                    SecondaryButton(
                        text = stringResource(R.string.action_cancel),
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun RangeSummaryRow(start: LocalDate?, end: LocalDate?, today: LocalDate, onClear: () -> Unit) {
    val startLabel = start?.let { it.label(today) } ?: stringResource(R.string.date_range_picker_start)
    val endLabel = end?.let { it.label(today) } ?: stringResource(R.string.date_range_picker_end)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            // A part is grey while it's still the Start/End placeholder and dark once a date fills
            // it; the dash goes dark with the start, since it only means something after one.
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = if (start != null) text_default else FutureDayColor)) { append("$startLabel – ") }
                withStyle(SpanStyle(color = if (end != null) text_default else FutureDayColor)) { append(endLabel) }
            },
            style = MaterialTheme.typography.regularBodyMedium,
            modifier = Modifier.weight(1f, fill = false)
        )
        Text(
            text = stringResource(R.string.action_clear),
            style = MaterialTheme.typography.semiboldBodySmall,
            color = if (start != null) text_error else text_error.copy(alpha = 0.4f),
            modifier = Modifier
                .clickable(enabled = start != null, onClick = onClear)
                .padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}

@Composable
private fun MonthHeader(visibleMonth: YearMonth, thisMonth: YearMonth, onMonthSelected: (YearMonth) -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 12.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .clickable { menuOpen = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = MonthFormatter.format(visibleMonth),
                    style = MaterialTheme.typography.semiboldBodySmall,
                    color = text_default
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = text_default
                )
            }
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
                modifier = Modifier.heightIn(max = 320.dp)
            ) {
                // Newest first — the months a rider is likely to want sit at the top.
                (0..MonthsBack).map { thisMonth.minusMonths(it) }.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(MonthFormatter.format(month), style = MaterialTheme.typography.regularBodyMedium) },
                        onClick = {
                            onMonthSelected(month)
                            menuOpen = false
                        }
                    )
                }
            }
        }
        // The arrows grey out at either end of the range, same colour as the future days — a disabled
        // IconButton alone only blocks the tap, it doesn't recolour a tint set explicitly.
        val canGoBack = visibleMonth.isAfter(thisMonth.minusMonths(MonthsBack))
        val canGoForward = visibleMonth.isBefore(thisMonth)
        IconButton(
            onClick = { onMonthSelected(visibleMonth.minusMonths(1)) },
            enabled = canGoBack
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.cd_previous_month),
                tint = if (canGoBack) text_default else FutureDayColor
            )
        }
        IconButton(
            onClick = { onMonthSelected(visibleMonth.plusMonths(1)) },
            enabled = canGoForward
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(R.string.cd_next_month),
                tint = if (canGoForward) text_default else FutureDayColor
            )
        }
    }
}

@Composable
private fun WeekdayRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp)
            .background(WeekdayBandColor, RoundedCornerShape(12.dp))
            .height(47.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WeekdayLetters.forEach { letter ->
            Text(
                text = letter,
                style = MaterialTheme.typography.regularBodyMedium,
                color = text_default,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    today: LocalDate,
    start: LocalDate?,
    end: LocalDate?,
    onDayClicked: (LocalDate) -> Unit
) {
    // Weeks start on Sunday, as the weekday row shows. dayOfWeek is Monday=1..Sunday=7, so % 7
    // turns Sunday into 0 and leaves Monday..Saturday as 1..6 — the leading blank cells.
    val leadingBlanks = month.atDay(1).dayOfWeek.value % 7
    val daysInMonth = month.lengthOfMonth()
    val weeks = (leadingBlanks + daysInMonth + 6) / 7
    Column(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(weeks) { week ->
            Row(modifier = Modifier.fillMaxWidth().height(CellHeight)) {
                repeat(7) { column ->
                    val day = week * 7 + column - leadingBlanks + 1
                    if (day in 1..daysInMonth) {
                        DayCell(
                            date = month.atDay(day),
                            today = today,
                            start = start,
                            end = end,
                            onClick = onDayClicked,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    today: LocalDate,
    start: LocalDate?,
    end: LocalDate?,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFuture = date.isAfter(today)
    val isStart = date == start
    val isEnd = date == end
    val isSelected = isStart || isEnd
    val hasRange = start != null && end != null && start != end
    val inRange = hasRange && !date.isBefore(start) && !date.isAfter(end)
    Box(
        modifier = modifier
            .fillMaxHeight()
            // The range band runs behind the cells edge to edge; at the two ends it only covers
            // the half facing the rest of the range, so it doesn't poke out past the rounded square.
            .drawBehind {
                if (inRange) {
                    val bandStart = if (isStart) size.width / 2 else 0f
                    val bandEnd = if (isEnd) size.width / 2 else size.width
                    drawRect(
                        color = surface_bg,
                        topLeft = Offset(bandStart, 0f),
                        size = Size(bandEnd - bandStart, size.height)
                    )
                }
            }
            .clickable(enabled = !isFuture) { onClick(date) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(CellHeight)
                .then(
                    if (isSelected) Modifier.background(surface_brand, RoundedCornerShape(8.dp)) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.regularBodyMedium,
                color = when {
                    isSelected -> text_inverted
                    isFuture -> FutureDayColor
                    date == today -> surface_brand
                    else -> text_default
                }
            )
        }
    }
}

// Shown without the year for the current one, with it otherwise — a range reaching back into last
// year would otherwise read the same as one from this year.
private fun LocalDate.label(today: LocalDate): String =
    (if (year == today.year) DayFormatter else DayWithYearFormatter).format(this)

// The callers (Report/History view models) convert from UTC-midnight millis, the form the platform
// picker reported — kept so they needed no change.
private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
