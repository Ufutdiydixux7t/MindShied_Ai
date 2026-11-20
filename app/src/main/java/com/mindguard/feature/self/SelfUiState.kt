package com.mindguard.feature.self

data class AppUsageInfo(
    val name: String,
    val usageTime: String,
    val isProtected: Boolean,
    val iconUrl: String? = null
)

data class DailyActivityStats(
    val sessions: Int,
    val usageTimeMinutes: Int,
    val videosCount: Int,
    val mostUsedApp: String
)

data class CurrentPeriodInfo(
    val startTime: String,
    val endTime: String,
    val usedMinutes: Int,
    val totalMinutes: Int,
    val nextPeriodStartTime: String
)

data class Achievement(
    val title: String,
    val isCompleted: Boolean
)

data class SelfUiState(
    val reelsUsedMinutes: Int = 35,
    val reelsLimitMinutes: Int = 50,
    val currentPeriod: CurrentPeriodInfo = CurrentPeriodInfo(
        startTime = "10:00 ص",
        endTime = "3:00 م",
        usedMinutes = 12,
        totalMinutes = 20,
        nextPeriodStartTime = "3:00 م"
    ),
    val protectionEnabled: Boolean = true,
    val protectedApps: List<AppUsageInfo> = listOf(
        AppUsageInfo("Instagram", "15 دقيقة", true, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/instagram.svg"),
        AppUsageInfo("TikTok", "10 دقائق", true, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/tiktok.svg"),
        AppUsageInfo("YouTube", "8 دقائق", true, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/youtube.svg"),
        AppUsageInfo("X", "2 دقيقة", true, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/x.svg")
    ),
    val activityStats: DailyActivityStats = DailyActivityStats(
        sessions = 8,
        usageTimeMinutes = 35,
        videosCount = 45,
        mostUsedApp = "Instagram"
    ),
    val aiTip: String = "استخدامك للمحتوى القصير يزداد في المساء. خذ استراحة قصيرة قبل بدء جلسة جديدة.",
    val achievements: List<Achievement> = listOf(
        Achievement("الالتزام بالحد اليومي", true),
        Achievement("أخذ استراحة", true),
        Achievement("توفير 20 دقيقة من وقت الشاشة", true)
    ),
    val importantAlert: String? = "وصلت إلى حد الاستخدام لهذه الفترة."
)
