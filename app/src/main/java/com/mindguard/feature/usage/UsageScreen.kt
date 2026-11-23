package com.mindguard.feature.usage

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.app.usage.UsageEvents
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.mindguard.core.components.AppTopBar
import com.mindguard.core.theme.Primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class RealAppUsage(
    val packageName: String,
    val name: String,
    val icon: Drawable?,
    val minutes: Int,
    val percentage: Float,
    val lastUsedMillis: Long,
    val launches: Int
)

private data class RealChartPoint(val label: String, val minutes: Int)

private data class RealStatistics(
    val totalMinutes: Int = 0,
    val reelsMinutes: Int = 0,
    val appUsage: List<RealAppUsage> = emptyList(),
    val chart: List<RealChartPoint> = emptyList(),
    val peakRange: String = "غير متاح",
    val peakDescription: String = "لا توجد بيانات استخدام كافية بعد.",
    val previousMinutes: Int = 0,
    val hasPermission: Boolean = true
)

private val reelsPackageNames = setOf(
    "com.instagram.android",
    "com.instagram.lite",
    "com.google.android.youtube",
    "com.zhiliaoapp.musically",
    "com.ss.android.ugc.trill",
    "com.facebook.katana",
    "com.facebook.lite"
)

@Composable
fun UsageScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showDetails by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var statistics by remember { mutableStateOf(RealStatistics()) }

    LaunchedEffect(selectedTab, refreshKey) {
        statistics = withContext(Dispatchers.Default) {
            loadRealStatistics(context, selectedTab)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L)
            refreshKey++
        }
    }

    if (showDetails) {
        DetailedUsageScreen(statistics = statistics, onBack = { showDetails = false })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Primary,
            divider = {},
            indicator = {},
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            listOf("اليوم", "الأسبوع", "الشهر").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = if (selectedTab == index) {
                        Modifier
                            .padding(horizontal = 3.dp, vertical = 4.dp)
                            .shadow(5.dp, RoundedCornerShape(18.dp))
                            .background(Primary, RoundedCornerShape(18.dp))
                    } else {
                        Modifier.padding(horizontal = 3.dp, vertical = 4.dp)
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!statistics.hasPermission) {
                UsagePermissionCard()
            }
            StatisticsSummaryCard(statistics)
            ScreenUsageCard(statistics)
            DailyUsageChartCard(statistics)
            PeakAndReelsRow(statistics)
            MostUsedAppsCard(statistics.appUsage)
            DetailedStatisticsButton(onClick = { showDetails = true })
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StatisticsSummaryCard(data: RealStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryTile("الاستهلاك الكلي", formatDuration(data.totalMinutes), Icons.Default.AccessTime, Modifier.weight(1f))
            SummaryTile("استخدام الشاشة", formatDuration(data.totalMinutes), Icons.Default.Apps, Modifier.weight(1f))
            SummaryTile("فيديوهات قصيرة", formatDuration(data.reelsMinutes), Icons.Default.Movie, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryTile(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(25.dp))
        Spacer(modifier = Modifier.height(5.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ScreenUsageCard(data: RealStatistics) {
    val comparison = if (data.previousMinutes > 0) {
        val difference = data.totalMinutes - data.previousMinutes
        val percentage = (kotlin.math.abs(difference).toFloat() / data.previousMinutes * 100).toInt()
        if (difference <= 0) "انخفاض $percentage% مقارنة بالفترة السابقة" else "ارتفاع $percentage% مقارنة بالفترة السابقة"
    } else {
        "لا توجد مقارنة سابقة متاحة"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("استخدام شاشة الهاتف", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Apps, contentDescription = null, tint = Primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(toArabicDigits(comparison), fontSize = 12.sp, color = if (data.totalMinutes <= data.previousMinutes || data.previousMinutes == 0) Color(0xFF159447) else Color(0xFFC43D4B))
                Text("مقارنة بالفترة السابقة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            UsageRing(data.totalMinutes, data.previousMinutes)
        }
    }
}

@Composable
private fun UsageRing(minutes: Int, previousMinutes: Int) {
    val progress = if (previousMinutes > 0) (minutes.toFloat() / previousMinutes).coerceIn(0f, 1f) else if (minutes > 0) 1f else 0f
    Box(modifier = Modifier.size(104.dp), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = Primary,
            trackColor = Color(0xFFDDE8FA),
            strokeWidth = 9.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(formatDuration(minutes), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("اليوم الحالي", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DailyUsageChartCard(data: RealStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("ساعات الاستخدام اليومية", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Default.BarChart, contentDescription = null, tint = Primary)
            }
            Spacer(modifier = Modifier.height(14.dp))
            if (data.chart.all { it.minutes == 0 }) {
                Text("لا توجد بيانات يومية كافية", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val max = data.chart.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 1
                    data.chart.forEach { point ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height((point.minutes.toFloat() / max * 105).coerceAtLeast(if (point.minutes > 0) 8f else 2f).dp)
                                    .background(Primary, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(toArabicDigits(point.label), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeakAndReelsRow(data: RealStatistics) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CircleInfoCard("أوقات الذروة", data.peakRange, "أكثر الأوقات استخداماً", Icons.Default.AccessTime, Modifier.weight(1f))
        CircleInfoCard("الفيديوهات القصيرة", formatDuration(data.reelsMinutes), "من إجمالي الاستخدام", Icons.Default.PlayArrow, Modifier.weight(1f))
    }
}

@Composable
private fun CircleInfoCard(title: String, value: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.CircleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)),
        border = BorderStroke(1.5.dp, Primary.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(23.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Primary, textAlign = TextAlign.Center)
            Text(subtitle, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun MostUsedAppsCard(apps: List<RealAppUsage>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("التطبيقات الأكثر استخداماً", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Default.Timeline, contentDescription = null, tint = Primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (apps.isEmpty()) {
                Text("لا توجد بيانات تطبيقات متاحة", modifier = Modifier.fillMaxWidth().padding(14.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                apps.take(5).forEachIndexed { index, app ->
                    RealAppUsageItem(index + 1, app)
                    if (index < apps.take(5).lastIndex) Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun RealAppUsageItem(rank: Int, app: RealAppUsage) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(toArabicDigits(rank.toString()), fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
            if (app.icon != null) {
                androidx.compose.foundation.Image(painter = rememberAsyncImagePainter(app.icon), contentDescription = app.name, modifier = Modifier.size(36.dp), contentScale = ContentScale.Fit)
            } else {
                Icon(Icons.Default.Apps, contentDescription = null, tint = Primary, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(app.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(formatDuration(app.minutes), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(progress = { app.percentage }, modifier = Modifier.fillMaxWidth().height(6.dp), color = Primary, trackColor = Color(0xFFE6EDF8))
            }
        }
    }
}

@Composable
private fun DetailedStatisticsButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary)
    ) {
        Text("إحصائيات التطبيق المفصلة", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.Default.ChevronLeft, contentDescription = null)
    }
}

@Composable
private fun UsagePermissionCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4DD))) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("فعّل صلاحية إحصائيات الاستخدام من إعدادات النظام لعرض بيانات حقيقية.", fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFB77900))
        }
    }
}

@Composable
private fun DetailedUsageScreen(statistics: RealStatistics, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(title = "إحصائيات التطبيق المفصلة", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "أعلى التطبيقات حسب وقت الاستخدام",
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End
            )
            Text(
                text = "بيانات حقيقية من إحصائيات استخدام الجهاز",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
            if (statistics.appUsage.isEmpty()) {
                Text("لا توجد بيانات تطبيقات متاحة حالياً", modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                statistics.appUsage.forEachIndexed { index, app ->
                    var expanded by remember(app.packageName) { mutableStateOf(false) }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Primary.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (app.icon != null) {
                                    androidx.compose.foundation.Image(
                                        painter = rememberAsyncImagePainter(app.icon),
                                        contentDescription = app.name,
                                        modifier = Modifier.size(42.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Icon(Icons.Default.Apps, contentDescription = null, tint = Primary, modifier = Modifier.size(42.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Text(app.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.End)
                                    Text(formatDuration(app.minutes), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End)
                                    Spacer(modifier = Modifier.height(5.dp))
                                    LinearProgressIndicator(
                                        progress = { app.percentage },
                                        modifier = Modifier.fillMaxWidth().height(6.dp),
                                        color = Primary,
                                        trackColor = Color(0xFFE6EDF8)
                                    )
                                }
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (expanded) "إخفاء التفاصيل" else "عرض التفاصيل",
                                    tint = Primary,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                            if (expanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                DetailedUsageRow("عدد مرات فتح التطبيق", toArabicDigits("${app.launches} مرات"))
                                DetailedUsageRow("إجمالي الوقت المقضي", formatDuration(app.minutes))
                                DetailedUsageRow("وقت الفيديوهات القصيرة", if (app.packageName in reelsPackageNames) formatDuration(app.minutes) else "لا ينطبق")
                                DetailedUsageRow("آخر استخدام", formatLastUsed(app.lastUsedMillis))
                                DetailedUsageRow("نسبة الاستخدام", toArabicDigits("${(app.percentage * 100).toInt()}%"))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailedUsageRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End)
    }
}

private fun loadRealStatistics(context: Context, tab: Int): RealStatistics {
    val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    if (!hasUsagePermission(context)) return RealStatistics(hasPermission = false)

    val now = System.currentTimeMillis()
    val end = now
    val start = when (tab) {
        0 -> startOfDayMillis(now)
        1 -> now - 7L * 24 * 60 * 60 * 1000
        else -> now - 30L * 24 * 60 * 60 * 1000
    }
    val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end).orEmpty()
    val packageManager = context.packageManager
    val ownPackage = context.packageName
    val usable = stats.filter { it.packageName != ownPackage && it.totalTimeInForeground > 0L }
    val totalMillis = usable.sumOf { it.totalTimeInForeground }
    val totalMinutes = (totalMillis / 60_000L).toInt()
    val reelsMinutes = (usable.filter { it.packageName in reelsPackageNames }.sumOf { it.totalTimeInForeground } / 60_000L).toInt()
    val launchCounts = countForegroundEvents(manager, start, end)
    val topApps = usable.sortedByDescending { it.totalTimeInForeground }.take(10).map { usage ->
        val appInfo = runCatching { packageManager.getApplicationInfo(usage.packageName, 0) }.getOrNull()
        val minutes = (usage.totalTimeInForeground / 60_000L).toInt()
        RealAppUsage(
            packageName = usage.packageName,
            name = appInfo?.loadLabel(packageManager)?.toString() ?: usage.packageName,
            icon = appInfo?.loadIcon(packageManager),
            minutes = minutes,
            percentage = if (totalMinutes > 0) minutes.toFloat() / totalMinutes else 0f,
            lastUsedMillis = usage.lastTimeUsed,
            launches = launchCounts[usage.packageName] ?: 0
        )
    }
    val chart = buildChart(manager, start, end, tab)
    val peak = chart.maxByOrNull { it.minutes }
    val previousMinutes = loadPreviousMinutes(context, manager, tab, now)

    return RealStatistics(
        totalMinutes = totalMinutes,
        reelsMinutes = reelsMinutes,
        appUsage = topApps,
        chart = chart,
        peakRange = peak?.label ?: "غير متاح",
        peakDescription = if (peak != null) "أعلى استخدام مسجل في هذه الفترة" else "لا توجد بيانات استخدام كافية بعد",
        previousMinutes = previousMinutes,
        hasPermission = true
    )
}

private fun buildChart(manager: UsageStatsManager, start: Long, end: Long, tab: Int): List<RealChartPoint> {
    val days = if (tab == 0) 6 else if (tab == 1) 7 else 7
    val bucketMillis = if (tab == 0) 4L * 60 * 60 * 1000 else if (tab == 1) 24L * 60 * 60 * 1000 else 4L * 24 * 60 * 60 * 1000
    val labels = if (tab == 0) listOf("00", "04", "08", "12", "16", "20") else (days - 1 downTo 0).map { offset -> LocalDate.now().minusDays(offset.toLong()).format(DateTimeFormatter.ofPattern("EEE", java.util.Locale("ar"))) }
    val result = mutableListOf<RealChartPoint>()
    for (index in labels.indices) {
        val bucketStart = if (tab == 0) start + index * bucketMillis else end - (labels.size - index) * bucketMillis
        val bucketEnd = (bucketStart + bucketMillis).coerceAtMost(end)
        val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, bucketStart, bucketEnd).orEmpty()
        val minutes = (stats.sumOf { it.totalTimeInForeground } / 60_000L).toInt()
        result += RealChartPoint(labels[index], minutes)
    }
    return result
}

private fun loadPreviousMinutes(context: Context, manager: UsageStatsManager, tab: Int, now: Long): Int {
    val duration = when (tab) { 0 -> 24L * 60 * 60 * 1000; 1 -> 7L * 24 * 60 * 60 * 1000; else -> 30L * 24 * 60 * 60 * 1000 }
    val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - duration * 2, now - duration).orEmpty()
    return (stats.filter { it.packageName != context.packageName }.sumOf { it.totalTimeInForeground } / 60_000L).toInt()
}

private fun hasUsagePermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun startOfDayMillis(now: Long): Long = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

private fun formatDuration(minutes: Int): String {
    if (minutes < 60) return "${toArabicDigits(minutes.toString())} دقيقة"
    return "${toArabicDigits((minutes / 60).toString())}س ${toArabicDigits((minutes % 60).toString())}د"
}

private fun formatLastUsed(timestamp: Long): String {
    if (timestamp <= 0L) return "غير متاح"
    return toArabicDigits(SimpleDateFormat("hh:mm a", Locale("ar")).format(Date(timestamp)))
}

private fun countForegroundEvents(manager: UsageStatsManager, start: Long, end: Long): Map<String, Int> {
    val counts = mutableMapOf<String, Int>()
    val events = manager.queryEvents(start, end)
    val event = UsageEvents.Event()
    while (events.hasNextEvent()) {
        events.getNextEvent(event)
        if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
            counts[event.packageName] = (counts[event.packageName] ?: 0) + 1
        }
    }
    return counts
}

private fun toArabicDigits(value: String): String = value.map { character ->
    when (character) {
        '0' -> '٠'
        '1' -> '١'
        '2' -> '٢'
        '3' -> '٣'
        '4' -> '٤'
        '5' -> '٥'
        '6' -> '٦'
        '7' -> '٧'
        '8' -> '٨'
        '9' -> '٩'
        else -> character
    }
}.joinToString("")
