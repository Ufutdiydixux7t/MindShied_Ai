package com.mindguard.feature.usage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mindguard.core.theme.DarkBlue
import com.mindguard.core.theme.Primary
import com.mindguard.core.theme.Secondary
import com.mindguard.core.theme.Success
import com.mindguard.core.theme.TextDark
import com.mindguard.core.theme.Warning

@Composable
fun UsageScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val uiState = remember { UsageUiState() }
    val currentData = when (selectedTabIndex) {
        0 -> uiState.dailyData
        1 -> uiState.weeklyData
        2 -> uiState.monthlyData
        else -> uiState.dailyData
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "الإحصائيات",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkBlue,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Primary,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Primary
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            val tabs = listOf("يومي", "أسبوعي", "شهري")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        if (uiState.isEmpty) {
            EmptyStatsView()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                UsageSummaryCard(currentData, isDaily = selectedTabIndex == 0)
                UsageChartCard(currentData, selectedTabIndex)
                AppUsageCard(currentData.appUsageList)
                PeakHoursCard(currentData.peakHourRange, currentData.peakHourDescription)
                if (selectedTabIndex == 0) {
                    PeriodAnalysisSection(currentData.periodAnalysis)
                }
                AiInsightCard(currentData.aiInsight)
                WeeklyGoalCard(
                    currentData.weeklyGoalTitle,
                    currentData.weeklyGoalProgress,
                    currentData.weeklyGoalComparison
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun UsageSummaryCard(data: StatisticsData, isDaily: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = if (isDaily) "استخدام اليوم" else if (data.chartPoints.size > 7) "استخدام الشهر" else "استخدام الأسبوع",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem("إجمالي الاستخدام", data.totalUsageTime, Modifier.weight(1f))
                SummaryItem("وقت الريلز", data.reelsTime, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem("عدد الجلسات", "${data.sessionsCount} جلسات", Modifier.weight(1f))
                SummaryItem("عدد الفيديوهات", "${data.videosWatched} فيديو", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)
    }
}

@Composable
fun UsageChartCard(data: StatisticsData, tabIndex: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "وقت الاستخدام",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                SimpleLineChart(data.chartPoints, data.dailyLimitMinutes.toFloat())
            }

            if (tabIndex == 0) {
                Text(
                    text = "الحد اليومي: ${data.dailyLimitMinutes} دقيقة",
                    style = MaterialTheme.typography.labelSmall,
                    color = Warning,
                    modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SimpleLineChart(points: List<ChartPoint>, limit: Float) {
    val primaryColor = Primary
    val warningColor = Warning

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (points.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val maxVal = (points.maxOf { it.value }.coerceAtLeast(limit)) * 1.2f

        val spaceX = width / (points.size - 1)

        val limitY = height - (limit / maxVal) * height
        drawLine(
            color = warningColor.copy(alpha = 0.3f),
            start = androidx.compose.ui.geometry.Offset(0f, limitY),
            end = androidx.compose.ui.geometry.Offset(width, limitY),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        val path = Path()
        points.forEachIndexed { i, pt ->
            val x = i * spaceX
            val y = height - (pt.value / maxVal) * height
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        points.forEachIndexed { i, pt ->
            val x = i * spaceX
            val y = height - (pt.value / maxVal) * height
            drawCircle(color = primaryColor, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x, y))
            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x, y))
        }
    }
}

@Composable
fun AppUsageCard(apps: List<AppUsageData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "استخدام التطبيقات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            apps.forEach { app ->
                AppUsageItem(app)
                if (app != apps.last()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                }
            }
        }
    }
}

@Composable
fun AppUsageItem(app: AppUsageData) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color(0xFFF1F5F9)) {
            if (app.iconUrl != null) {
                AsyncImage(
                    model = app.iconUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(Icons.Default.Apps, contentDescription = null, tint = DarkBlue, modifier = Modifier.padding(8.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = app.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextDark)
                Text(text = "${app.minutes} دقيقة", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { app.percentage },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (app.name == "Instagram") Primary else Secondary,
                trackColor = Color(0xFFF1F5F9)
            )
        }
    }
}

@Composable
fun PeakHoursCard(range: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = Color.White) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Primary, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "أوقات الذروة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = DarkBlue)
                Text(text = range, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Primary)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PeriodAnalysisSection(periods: List<UsagePeriod>) {
    Column {
        Text(
            text = "تحليل الفترات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBlue,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            periods.forEach { period ->
                PeriodCard(period)
            }
        }
    }
}

@Composable
fun PeriodCard(period: UsagePeriod) {
    val progress = period.usedMinutes.toFloat() / period.totalMinutes.toFloat()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = period.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextDark)
                    if (period.isCompleted) {
                        Text(text = "مكتملة", style = MaterialTheme.typography.labelSmall, color = Success, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (period.isCompleted) Success else Primary,
                    trackColor = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${period.usedMinutes} / ${period.totalMinutes} دقيقة", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AiInsightCard(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "تحليل MindGuard", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = insight, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f), lineHeight = 22.sp)
        }
    }
}

@Composable
fun WeeklyGoalCard(title: String, progress: Float, comparison: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "هدف هذا الأسبوع", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = DarkBlue)
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(50.dp)) {
                    CircularProgressIndicator(progress = { progress }, color = Success, strokeWidth = 4.dp, trackColor = Color(0xFFE8F5E9))
                    Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Success)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "${(progress * 100).toInt()}% من الهدف", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = comparison, style = MaterialTheme.typography.labelSmall, color = Success)
                }
            }
        }
    }
}

@Composable
fun EmptyStatsView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "لا توجد بيانات كافية حتى الآن", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkBlue)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "استخدم التطبيقات لبعض الوقت حتى يبدأ MindGuard في تحليل نمط استخدامك.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}
