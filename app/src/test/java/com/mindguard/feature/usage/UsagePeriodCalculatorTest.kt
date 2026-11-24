package com.mindguard.feature.usage

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UsagePeriodCalculatorTest {
    private val today = LocalDate.of(2026, 11, 24)

    @Test
    fun dailyRangeUsesLocalCalendarMidnightBoundaries() {
        val range = currentUsageDateRange(0, today, Locale.US)
        assertEquals(today, range.startInclusive)
        assertEquals(today.plusDays(1), range.endExclusive)
        assertEquals(1L, ChronoUnit.DAYS.between(range.startInclusive, range.endExclusive))
    }

    @Test
    fun weeklyRangeUsesTheLocaleFirstDayAndExactlySevenDays() {
        listOf(Locale.US, Locale.FRANCE, Locale("ar", "SA")).forEach { locale ->
            val range = currentUsageDateRange(1, today, locale)
            val expectedStart = today.minusDays(
                ((today.dayOfWeek.value - WeekFields.of(locale).firstDayOfWeek.value + 7) % 7).toLong()
            )
            assertEquals(expectedStart, range.startInclusive)
            assertEquals(7L, ChronoUnit.DAYS.between(range.startInclusive, range.endExclusive))
            assertTrue(today >= range.startInclusive && today < range.endExclusive)
        }
    }

    @Test
    fun monthlyRangeUsesGregorianMonthAndPreviousCalendarMonth() {
        val current = currentUsageDateRange(2, today, Locale.US)
        val previous = previousUsageDateRange(2, today, Locale.US)
        assertEquals(LocalDate.of(2026, 11, 1), current.startInclusive)
        assertEquals(LocalDate.of(2026, 12, 1), current.endExclusive)
        assertEquals(LocalDate.of(2026, 10, 1), previous.startInclusive)
        assertEquals(current.startInclusive, previous.endExclusive)
    }

    @Test
    fun dayAndWeekChartsCoverTheirWholeCalendarPeriods() {
        val day = usageChartBuckets(0, today, Locale.US)
        assertEquals(6, day.size)
        assertEquals(today.atStartOfDay(), day.first().start)
        assertEquals(today.plusDays(1).atStartOfDay(), day.last().endExclusive)
        assertTrue(day.zipWithNext().all { (left, right) -> left.endExclusive == right.start })

        val week = usageChartBuckets(1, today, Locale.FRANCE)
        val range = currentUsageDateRange(1, today, Locale.FRANCE)
        assertEquals(7, week.size)
        assertEquals(range.startInclusive.atStartOfDay(), week.first().start)
        assertEquals(range.endExclusive.atStartOfDay(), week.last().endExclusive)
    }

    @Test
    fun monthlyChartCoversEveryDayWithoutGapsOrOverlap() {
        val buckets = usageChartBuckets(2, today, Locale.FRANCE)
        val month = currentUsageDateRange(2, today, Locale.FRANCE)
        assertEquals(month.startInclusive.atStartOfDay(), buckets.first().start)
        assertEquals(month.endExclusive.atStartOfDay(), buckets.last().endExclusive)
        assertTrue(buckets.size in 4..6)
        assertTrue(buckets.zipWithNext().all { (left, right) -> left.endExclusive == right.start })
    }
    @Test
    fun dailyRangeChangesAtLocalDateBoundaryNotA24HourLookback() {
        val beforeMidnight = LocalDate.of(2026, 11, 24)
        val afterMidnight = beforeMidnight.plusDays(1)
        val oldRange = currentUsageDateRange(0, beforeMidnight, Locale.US)
        val newRange = currentUsageDateRange(0, afterMidnight, Locale.US)
        assertEquals(beforeMidnight, oldRange.startInclusive)
        assertEquals(afterMidnight, newRange.startInclusive)
        assertFalse(oldRange.startInclusive == newRange.startInclusive)
    }

    @Test
    fun monthlyChartHasVariableBucketCountAndUsesCurrentGregorianMonth() {
        val february = LocalDate.of(2025, 2, 18)
        val buckets = usageChartBuckets(2, february, Locale.US)
        assertEquals(LocalDate.of(2025, 2, 1).atStartOfDay(), buckets.first().start)
        assertEquals(LocalDate.of(2025, 3, 1).atStartOfDay(), buckets.last().endExclusive)
        assertTrue(buckets.size in 4..6)
        assertTrue(buckets.zipWithNext().all { (left, right) -> left.endExclusive == right.start })
    }

}
