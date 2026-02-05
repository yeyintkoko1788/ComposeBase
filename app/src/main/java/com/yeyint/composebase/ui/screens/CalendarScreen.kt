package com.yeyint.composebase.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.HorizontalYearCalendar
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.yearcalendar.rememberYearCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
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
import java.time.DayOfWeek
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    contentPadding: PaddingValues
) {
    val currentYear = remember { Year.now() }
    val startYear = remember { currentYear.minusYears(1) } // Adjust as needed
    val endYear = remember { currentYear.plusYears(1) } // Adjust as needed
    //val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)

    val state = rememberCalendarState(
        firstDayOfWeek = daysOfWeek.first(),
        startMonth = YearMonth.of(startYear.value, 1),
        endMonth = YearMonth.of(endYear.value, 12),
        firstVisibleMonth = YearMonth.now()
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        VerticalCalendar(
            state = state,
            dayContent = { Day(it) },
            calendarScrollPaged = true,
            userScrollEnabled = true,
            contentPadding = PaddingValues(8.dp),
            monthHeader = { month ->
                // You may want to use `remember {}` here so the mapping is not done
                // every time as the days of week order will never change unless
                // you set a new value for `firstDayOfWeek` in the state.
                val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                MonthHeader(month = month.yearMonth.month.name.plus(" ${month.yearMonth.year}"), daysOfWeek = daysOfWeek)
            }
        )
    }
}

@Composable
fun MonthHeader(month : String,daysOfWeek: List<DayOfWeek>) {
    Column() {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.boldHeading3,
            text = month,
        )
        DaysOfWeekTitle(daysOfWeek)
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.semiboldBodyMedium
            )
        }
    }
}

@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = n04,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.padding(top = 8.dp))
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (day.position == DayPosition.MonthDate) text_dark else text_light,
                style = MaterialTheme.typography.mediumBodyMedium
            )
        }

    }
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