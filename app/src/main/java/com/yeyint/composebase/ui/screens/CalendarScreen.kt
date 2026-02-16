package com.yeyint.composebase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.HorizontalYearCalendar
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.yearcalendar.rememberYearCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.yeyint.composebase.ui.theme.BasePreviewWrapper
import com.yeyint.composebase.ui.theme.boldBodyLarge
import com.yeyint.composebase.ui.theme.boldHeading3
import com.yeyint.composebase.ui.theme.mediumBodyMedium
import com.yeyint.composebase.ui.theme.n04
import com.yeyint.composebase.ui.theme.n06
import com.yeyint.composebase.ui.theme.n08
import com.yeyint.composebase.ui.theme.semiboldBodyMedium
import com.yeyint.composebase.ui.theme.text_dark
import com.yeyint.composebase.ui.theme.text_light
import com.yeyint.composebase.ui.theme.text_primary
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(contentPadding: PaddingValues) {
    val daysOfWeek = remember { daysOfWeek(DayOfWeek.SUNDAY) }

    val currentYear = YearMonth.now().year

//    val state = rememberCalendarState(
//        firstDayOfWeek = daysOfWeek.first(),
//        startMonth = YearMonth.of(currentYear, 1),   // Jan of current year
//        endMonth = YearMonth.of(currentYear, 12),    // Dec of current year
//        firstVisibleMonth = YearMonth.now()
//    )
    val state = rememberCalendarState(
        startMonth = YearMonth.of(currentYear, 1),
        endMonth = YearMonth.of(currentYear, 12),
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid,
    )

    val visibleMonth by remember {
        derivedStateOf { state.firstVisibleMonth.yearMonth }
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {


        // 🔒 Sticky Month / Year with controls
        MonthHeaderWithControls(
            visibleMonth = visibleMonth,
            onPrevMonth = {
                scope.launch {
                    state.animateScrollToMonth(visibleMonth.minusMonths(1))
                }
            },
            onNextMonth = {
                scope.launch {
                    state.animateScrollToMonth(visibleMonth.plusMonths(1))
                }
            }
        )


        VerticalCalendar(
            state = state,
            calendarScrollPaged = true,
            userScrollEnabled = true,
            contentHeightMode = ContentHeightMode.Fill,
            contentPadding = PaddingValues(horizontal = 8.dp),
            dayContent = { Day(it) },
            monthHeader = { month ->
                val daysOfWeek = remember(month.yearMonth) {
                    month.weekDays.first().map { it.date.dayOfWeek }
                }
                DaysOfWeekTitle(daysOfWeek)
            }
        )
    }
}


@Composable
fun MonthHeaderWithControls(
    visibleMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${visibleMonth.year}",
            style = MaterialTheme.typography.boldHeading3,
            modifier = Modifier.weight(1f)
        )

        Row {
            IconButton(onClick = onPrevMonth) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Previous month"
                )
            }
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Next month"
                )
            }
        }
    }
}

//@Composable
//fun MonthHeader(month : String,daysOfWeek: List<DayOfWeek>) {
//    Column() {
//        Text(
//            modifier = Modifier.padding(start = 12.dp),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.boldHeading3,
//            text = month,
//        )
//        DaysOfWeekTitle(daysOfWeek)
//    }
//}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth().background(n04)) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.semiboldBodyMedium
            )
        }
    }
}

@Composable
fun Day(day: CalendarDay) {
    val today = remember { LocalDate.now() }
    val isToday = day.date == today
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(32.dp)
                .then(
                    if (isToday)
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = when {
                    isToday -> text_primary
                    day.position == DayPosition.MonthDate -> text_dark
                    else -> text_light
                },
                style = MaterialTheme.typography.mediumBodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
    HorizontalDivider(color = n04, thickness = 1.dp)
}






@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    BasePreviewWrapper() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            CalendarScreen(
                contentPadding = PaddingValues()
            )
        }
    }
}