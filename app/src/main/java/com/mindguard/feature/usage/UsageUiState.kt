package com.mindguard.feature.usage

/** Data structures for usage values obtained from the device; no sample values are provided. */
data class ChartPoint(val label: String, val value: Float)

data class AppUsageData(
    val name: String,
    val minutes: Int,
    val percentage: Float,
    val iconUrl: String? = null
)

data class UsagePeriod(
    val name: String,
    val usedMinutes: Int,
    val totalMinutes: Int,
    val isCompleted: Boolean = usedMinutes >= totalMinutes
)

data class StatisticsData(
    val totalUsageTime: String? = null,
    val reelsTime: String? = null,
    /** Number of foreground transitions, not a guaranteed number of app launches. */
    val sessionsCount: Int? = null,
    /** Null until the app has a reliable, explicitly measured video counter. */
    val videosWatched: Int? = null,
    val chartPoints: List<ChartPoint> = emptyList(),
    val appUsageList: List<AppUsageData> = emptyList(),
    val peakHourRange: String? = null,
    val peakHourDescription: String? = null,
    val periodAnalysis: List<UsagePeriod> = emptyList(),
    val aiInsight: String? = null,
    val weeklyGoalTitle: String? = null,
    val weeklyGoalProgress: Float? = null,
    val weeklyGoalComparison: String? = null,
    val dailyLimitMinutes: Int? = null
)

data class UsageUiState(
    val selectedTab: Int = 0,
    val dailyData: StatisticsData? = null,
    val weeklyData: StatisticsData? = null,
    val monthlyData: StatisticsData? = null,
    val isLoading: Boolean = true,
    val hasUsagePermission: Boolean? = null,
    val hasData: Boolean = false
)
