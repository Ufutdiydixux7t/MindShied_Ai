package com.mindguard.feature.usage

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

/** A calendar interval with an inclusive start date and exclusive end date. */
data class UsageDateRange(
    val startInclusive: LocalDate,
    val endExclusive: LocalDate
)

data class UsageChartBucket(
    val label: String,
    val start: LocalDateTime,
    val endExclusive: LocalDateTime
)

/** Uses ISO/Gregorian dates and the locale's first weekday for calendar weeks. */
fun currentUsageDateRange(tab: Int, today: LocalDate, locale: Locale): UsageDateRange = when (tab) {
    0 -> UsageDateRange(today, today.plusDays(1))
    1 -> {
        val firstDay = WeekFields.of(locale).firstDayOfWeek
        val start = today.with(TemporalAdjusters.previousOrSame(firstDay))
        UsageDateRange(start, start.plusWeeks(1))
    }
    else -> {
        val start = YearMonth.from(today).atDay(1)
        UsageDateRange(start, start.plusMonths(1))
    }
}

fun previousUsageDateRange(tab: Int, today: LocalDate, locale: Locale): UsageDateRange {
    val current = currentUsageDateRange(tab, today, locale)
    return when (tab) {
        0 -> UsageDateRange(current.startInclusive.minusDays(1), current.startInclusive)
        1 -> UsageDateRange(current.startInclusive.minusWeeks(1), current.startInclusive)
        else -> UsageDateRange(current.startInclusive.minusMonths(1), current.startInclusive)
    }
}

/**
 * Daily charts use six four-hour local-time blocks; weekly charts use each day
 * of the locale's current week; monthly charts use the calendar weeks that
 * intersect the current Gregorian month.
 */
fun usageChartBuckets(tab: Int, today: LocalDate, locale: Locale): List<UsageChartBucket> = when (tab) {
    0 -> (0 until 6).map { index ->
        val startHour = index * 4
        val endHour = startHour + 4
        UsageChartBucket(
            label = "%02d–%02d".format(Locale.ROOT, startHour, endHour),
            start = LocalDateTime.of(today, LocalTime.of(startHour, 0)),
            endExclusive = if (endHour == 24) {
                LocalDateTime.of(today.plusDays(1), LocalTime.MIDNIGHT)
            } else {
                LocalDateTime.of(today, LocalTime.of(endHour, 0))
            }
        )
    }
    1 -> {
        val range = currentUsageDateRange(1, today, locale)
        (0 until 7).map { offset ->
            val date = range.startInclusive.plusDays(offset.toLong())
            UsageChartBucket(
                label = date.format(java.time.format.DateTimeFormatter.ofPattern("EEE", locale)),
                start = LocalDateTime.of(date, LocalTime.MIDNIGHT),
                endExclusive = LocalDateTime.of(date.plusDays(1), LocalTime.MIDNIGHT)
            )
        }
    }
    else -> {
        val monthStart = YearMonth.from(today).atDay(1)
        val monthEnd = monthStart.plusMonths(1)
        val firstDay = WeekFields.of(locale).firstDayOfWeek
        buildList {
            var cursor = monthStart
            var weekIndex = 1
            while (cursor < monthEnd) {
                val offsetFromWeekStart = (cursor.dayOfWeek.value - firstDay.value + 7) % 7
                val daysToNextWeek = 7 - offsetFromWeekStart
                val next = cursor.plusDays(daysToNextWeek.toLong()).coerceAtMost(monthEnd)
                add(
                    UsageChartBucket(
                        label = "الأسبوع $weekIndex",
                        start = LocalDateTime.of(cursor, LocalTime.MIDNIGHT),
                        endExclusive = LocalDateTime.of(next, LocalTime.MIDNIGHT)
                    )
                )
                cursor = next
                weekIndex++
            }
        }
    }
}
