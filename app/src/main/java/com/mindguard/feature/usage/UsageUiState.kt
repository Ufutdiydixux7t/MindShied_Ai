package com.mindguard.feature.usage

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
    val totalUsageTime: String,
    val reelsTime: String,
    val sessionsCount: Int,
    val videosWatched: Int,
    val chartPoints: List<ChartPoint>,
    val appUsageList: List<AppUsageData>,
    val peakHourRange: String,
    val peakHourDescription: String,
    val periodAnalysis: List<UsagePeriod>,
    val aiInsight: String,
    val weeklyGoalTitle: String,
    val weeklyGoalProgress: Float,
    val weeklyGoalComparison: String,
    val dailyLimitMinutes: Int = 50
)

data class UsageUiState(
    val selectedTab: Int = 0,
    val dailyData: StatisticsData = StatisticsData(
        totalUsageTime = "35 دقيقة",
        reelsTime = "35 دقيقة",
        sessionsCount = 8,
        videosWatched = 45,
        chartPoints = listOf(
            ChartPoint("8ص", 5f),
            ChartPoint("10ص", 10f),
            ChartPoint("12م", 15f),
            ChartPoint("2م", 8f),
            ChartPoint("4م", 20f),
            ChartPoint("6م", 25f),
            ChartPoint("8م", 40f),
            ChartPoint("10م", 15f)
        ),
        appUsageList = listOf(
            AppUsageData("Instagram", 15, 0.45f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/instagram.svg"),
            AppUsageData("TikTok", 12, 0.35f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/tiktok.svg"),
            AppUsageData("YouTube", 8, 0.15f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/youtube.svg"),
            AppUsageData("X", 5, 0.05f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/x.svg")
        ),
        peakHourRange = "8:00 - 9:00 مساءً",
        peakHourDescription = "هذه الفترة هي أعلى فترة استخدام اليوم.",
        periodAnalysis = listOf(
            UsagePeriod("الفترة الأولى", 10, 20),
            UsagePeriod("الفترة الثانية", 5, 20),
            UsagePeriod("الفترة الثالثة", 20, 20),
            UsagePeriod("الفترة الخامسة", 0, 20)
        ),
        aiInsight = "لاحظنا أن استخدامك للمحتوى القصير يزداد في المساء. أعلى فترة استخدام لديك بين 8 و9 مساءً. حاول تقليل استخدامك في هذه الفترة وأخذ استراحة قصيرة.",
        weeklyGoalTitle = "تقليل استخدام المحتوى القصير بنسبة 10%",
        weeklyGoalProgress = 0.65f,
        weeklyGoalComparison = "أفضل من الأسبوع الماضي بـ 8%"
    ),
    val weeklyData: StatisticsData = StatisticsData(
        totalUsageTime = "4.2 ساعة",
        reelsTime = "3.8 ساعة",
        sessionsCount = 52,
        videosWatched = 310,
        chartPoints = listOf(
            ChartPoint("السبت", 45f),
            ChartPoint("الأحد", 55f),
            ChartPoint("الاثنين", 30f),
            ChartPoint("الثلاثاء", 40f),
            ChartPoint("الأربعاء", 60f),
            ChartPoint("الخميس", 80f),
            ChartPoint("الجمعة", 50f)
        ),
        appUsageList = listOf(
            AppUsageData("Instagram", 120, 0.5f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/instagram.svg"),
            AppUsageData("TikTok", 90, 0.3f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/tiktok.svg"),
            AppUsageData("YouTube", 40, 0.15f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/youtube.svg"),
            AppUsageData("X", 10, 0.05f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/x.svg")
        ),
        peakHourRange = "الخميس",
        peakHourDescription = "كان يوم الخميس هو الأكثر استهلاكاً هذا الأسبوع.",
        periodAnalysis = listOf(),
        aiInsight = "أداء ممتاز هذا الأسبوع! لقد قمت بتقليل وقت الشاشة الإجمالي بنسبة 12% مقارنة بالأسبوع الماضي.",
        weeklyGoalTitle = "تقليل استخدام المحتوى القصير بنسبة 10%",
        weeklyGoalProgress = 0.85f,
        weeklyGoalComparison = "أفضل من الأسبوع الماضي بـ 12%"
    ),
    val monthlyData: StatisticsData = StatisticsData(
        totalUsageTime = "18 ساعة",
        reelsTime = "16.5 ساعة",
        sessionsCount = 210,
        videosWatched = 1240,
        chartPoints = listOf(
            ChartPoint("الأسبوع 1", 350f),
            ChartPoint("الأسبوع 2", 420f),
            ChartPoint("الأسبوع 3", 380f),
            ChartPoint("الأسبوع 4", 300f)
        ),
        appUsageList = listOf(
            AppUsageData("Instagram", 500, 0.55f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/instagram.svg"),
            AppUsageData("TikTok", 400, 0.3f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/tiktok.svg"),
            AppUsageData("YouTube", 120, 0.1f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/youtube.svg"),
            AppUsageData("X", 50, 0.05f, "https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/x.svg")
        ),
        peakHourRange = "الأسبوع 2",
        peakHourDescription = "الأسبوع الثاني شهد أعلى معدل نشاط لهذا الشهر.",
        periodAnalysis = listOf(),
        aiInsight = "يظهر تحسن ملحوظ في التزامك بالحدود اليومية في نهاية الشهر. استمر على هذا المنوال!",
        weeklyGoalTitle = "تحسين التوازن الرقمي الشهري",
        weeklyGoalProgress = 0.75f,
        weeklyGoalComparison = "متوسط استخدامك اليومي: 40 دقيقة"
    ),
    val isEmpty: Boolean = false
)
