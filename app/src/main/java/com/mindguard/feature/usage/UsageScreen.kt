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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import coil.compose.rememberAsyncImagePainter
import com.mindguard.core.components.AppTopBar
import com.mindguard.core.theme.Primary
import com.mindguard.platform.accessibility.ProtectionPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.YearMonth
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
    val launches: Int,
    val trackedReelsSeconds: Long? = null
)

private data class RealChartPoint(val label: String, val minutes: Int)

private data class RealStatistics(
    val totalMinutes: Int = 0,
    val reelsSeconds: Long? = null,
    val appUsage: List<RealAppUsage> = emptyList(),
    val chart: List<RealChartPoint> = emptyList(),
    val peakRange: String? = null,
    val peakDescription: String = "لا توجد بيانات استخدام كافية بعد.",
    val previousMinutes: Int? = null,
    val hasPermission: Boolean = false,
    val hasData: Boolean = false,
    val isLoading: Boolean = false
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
fun UsageScreen(
    onDetailVisibilityChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showDetails by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var statistics by remember { mutableStateOf(RealStatistics(isLoading = true)) }

    LaunchedEffect(selectedTab, refreshKey) {
        statistics = RealStatistics(isLoading = true, hasPermission = statistics.hasPermission)
        statistics = withContext(Dispatchers.Default) {
            loadRealStatistics(context, selectedTab)
        }
    }

    LaunchedEffect(showDetails) {
        onDetailVisibilityChange(!showDetails)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L)
            refreshKey++
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (showDetails) {
            DetailedUsageScreen(statistics = statistics, onBack = { showDetails = false })
        } else Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
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
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp))
                    } else {
                        Modifier.padding(horizontal = 3.dp, vertical = 4.dp)
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
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
            if (!statistics.isLoading && !statistics.hasPermission) {
                UsagePermissionCard()
            }
            StatisticsSummaryCard(statistics)
            ScreenUsageCard(statistics, selectedTab)
            DailyUsageChartCard(statistics, selectedTab)
            PeakAndReelsRow(statistics)
            MostUsedAppsCard(statistics.appUsage.take(5))
            DetailedStatisticsButton(onClick = { showDetails = true })
            Spacer(modifier = Modifier.height(20.dp))
        }
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
            SummaryTile("الاستهلاك الكلي", usageTotalLabel(data), Icons.Default.AccessTime, Modifier.weight(1f))
            SummaryTile("عدد التطبيقات", appCountLabel(data), Icons.Default.Apps, Modifier.weight(1f))
            SummaryTile("فيديوهات قصيرة", data.reelsSeconds?.let(::formatDurationSeconds) ?: "غير متاح", Icons.Default.Movie, Modifier.weight(1f))
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
private fun ScreenUsageCard(data: RealStatistics, tab: Int) {
    val previousMinutes = data.previousMinutes
    val comparison = when {
        data.isLoading -> "جارٍ تحميل بيانات الاستخدام"
        !data.hasPermission -> "تتطلب هذه الإحصائية صلاحية الاستخدام"
        !data.hasData -> "لا توجد بيانات لهذه الفترة"
        previousMinutes == null || previousMinutes <= 0 -> "لا توجد مقارنة سابقة متاحة"
        else -> {
            val difference = data.totalMinutes - previousMinutes
            val percentage = (kotlin.math.abs(difference).toFloat() / previousMinutes * 100).toInt()
            if (difference <= 0) "انخفاض $percentage% مقارنة بالفترة السابقة" else "ارتفاع $percentage% مقارنة بالفترة السابقة"
        }
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
                    Icon(Icons.Default.Apps, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("استخدام شاشة الهاتف", fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Start)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(toArabicDigits(comparison), fontSize = 12.sp, color = if (previousMinutes == null || data.totalMinutes <= previousMinutes) Color(0xFF159447) else Color(0xFFC43D4B), textAlign = TextAlign.Start)
                Text("مقارنة بالفترة السابقة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
            }
            UsageRing(data.totalMinutes, tab, data.hasData && data.hasPermission)
        }
    }
}

@Composable
private fun UsageRing(minutes: Int, tab: Int, hasData: Boolean) {
    val periodMinutes = when (tab) {
        0 -> 24 * 60
        1 -> 7 * 24 * 60
        else -> YearMonth.now(ZoneId.systemDefault()).lengthOfMonth() * 24 * 60
    }
    val progress = if (hasData) (minutes.toFloat() / periodMinutes).coerceIn(0f, 1f) else 0f
    Box(modifier = Modifier.size(88.dp), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 9.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (hasData) formatDuration(minutes) else "غير متاح", fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 1)
            Text(periodLabel(tab), fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DailyUsageChartCard(data: RealStatistics, tab: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(chartTitle(tab), fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (data.isLoading) {
                Text("جارٍ تحميل البيانات", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else if (!data.hasPermission) {
                Text("تتطلب بيانات الاستخدام صلاحية من إعدادات الجهاز", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else if (!data.hasData || data.chart.all { it.minutes == 0 }) {
                Text("لا توجد بيانات استخدام مسجلة لهذه الفترة", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().height(130.dp),
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
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
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
        MetricInfoCard("أوقات الذروة", data.peakRange ?: "غير متاح", data.peakDescription, Icons.Default.AccessTime, Modifier.weight(1f))
        MetricInfoCard(
            "استهلاك الريلز",
            data.reelsSeconds?.let(::formatDurationSeconds) ?: "غير متاح",
            if (data.reelsSeconds == null) "لا تتوفر بيانات رصد محلية" else "من المقاطع التي رصدها التطبيق",
            Icons.Default.PlayArrow,
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricInfoCard(title: String, value: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(19.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
            Text(subtitle, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun MostUsedAppsCard(apps: List<RealAppUsage>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timeline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("التطبيقات الأكثر استخداماً", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
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
            if (app.icon != null) {
                androidx.compose.foundation.Image(painter = rememberAsyncImagePainter(app.icon), contentDescription = null, modifier = Modifier.size(34.dp), contentScale = ContentScale.Fit)
            } else {
                Icon(Icons.Default.Apps, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(app.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Start, maxLines = 1)
                    Text(formatDuration(app.minutes), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(progress = { app.percentage }, modifier = Modifier.fillMaxWidth().height(6.dp), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(toArabicDigits(rank.toString()), fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DetailedStatisticsButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("إحصائيات التطبيق المفصلة", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.Default.ChevronLeft, contentDescription = null)
    }
}

@Composable
private fun ShowMoreButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("عرض المزيد", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
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
    var visibleAppCount by remember { mutableIntStateOf(5) }

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
                fontSize = 17.sp,
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
            if (statistics.isLoading) {
                Text("جارٍ تحميل بيانات الاستخدام", modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (!statistics.hasPermission) {
                Text("تتطلب بيانات التطبيقات صلاحية الاستخدام من إعدادات الجهاز", modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (statistics.appUsage.isEmpty()) {
                Text("لا توجد بيانات تطبيقات مسجلة لهذه الفترة", modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                statistics.appUsage.take(visibleAppCount).forEachIndexed { index, app ->
                    var expanded by remember(app.packageName) { mutableStateOf(false) }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (app.icon != null) {
                                    androidx.compose.foundation.Image(
                                        painter = rememberAsyncImagePainter(app.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Icon(Icons.Default.Apps, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                                    Text(app.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Start, maxLines = 1)
                                    Text(formatDuration(app.minutes), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { app.percentage },
                                        modifier = Modifier.fillMaxWidth().height(5.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (expanded) "إخفاء التفاصيل" else "عرض التفاصيل",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                            if (expanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                DetailedUsageRow("مرات الانتقال إلى التطبيق", toArabicDigits("${app.launches} مرات"))
                                DetailedUsageRow("إجمالي الوقت المقضي", formatDuration(app.minutes))
                                DetailedUsageRow("وقت الفيديوهات القصيرة المَرْصودة", app.trackedReelsSeconds?.let(::formatDurationSeconds) ?: if (app.packageName in reelsPackageNames) "غير متاح" else "لا ينطبق")
                                DetailedUsageRow("آخر استخدام", formatLastUsed(app.lastUsedMillis))
                                DetailedUsageRow("نسبة الاستخدام", toArabicDigits("${(app.percentage * 100).toInt()}%"))
                            }
                        }
                    }
                }
                if (visibleAppCount < statistics.appUsage.size) {
                    ShowMoreButton(onClick = { visibleAppCount += 5 })
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
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun loadRealStatistics(context: Context, tab: Int): RealStatistics {
    if (!hasUsagePermission(context)) return RealStatistics(hasPermission = false)

    val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val zone = ZoneId.systemDefault()
    val locale = Locale.getDefault()
    val nowMillis = System.currentTimeMillis()
    val today = LocalDate.now(zone)
    val range = currentUsageDateRange(tab, today, locale)
    val startMillis = range.startInclusive.atStartOfDay(zone).toInstant().toEpochMilli()
    val endMillis = minOf(range.endExclusive.atStartOfDay(zone).toInstant().toEpochMilli(), nowMillis)
    val previousRange = previousUsageDateRange(tab, today, locale)
    val previousStart = previousRange.startInclusive.atStartOfDay(zone).toInstant().toEpochMilli()
    val previousEnd = minOf(previousRange.endExclusive.atStartOfDay(zone).toInstant().toEpochMilli(), startMillis)
    val ownPackage = context.packageName
    val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startMillis, endMillis).orEmpty()
    val aggregated = stats.filter { it.packageName != ownPackage && it.totalTimeInForeground > 0L }
        .groupBy { it.packageName }
        .mapValues { (_, entries) ->
            entries.sumOf { it.totalTimeInForeground } to entries.maxOfOrNull { it.lastTimeUsed }.orZero()
        }
    val totalMillis = aggregated.values.sumOf { it.first }
    val totalMinutes = (totalMillis / 60_000L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    val packageManager = context.packageManager
    val usageStats = aggregated.entries.sortedByDescending { it.value.first }
    val allTrackedPackages = reelsPackageNames.filter {
        ProtectionPreferences.hasRecordedUsageBetween(context, it, range.startInclusive, range.endExclusive)
    }
    val reelsHasData = allTrackedPackages.isNotEmpty()
    val reelsSeconds = if (reelsHasData) allTrackedPackages.sumOf {
        ProtectionPreferences.getRecordedUsageSecondsBetween(context, it, range.startInclusive, range.endExclusive)
    } else null
    val launchCounts = countForegroundEvents(manager, startMillis, endMillis)
    val topApps = usageStats.map { (packageName, totals) ->
        val appInfo = runCatching { packageManager.getApplicationInfo(packageName, 0) }.getOrNull()
        val minutes = (totals.first / 60_000L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val hasReelsRecord = packageName in reelsPackageNames &&
            ProtectionPreferences.hasRecordedUsageBetween(context, packageName, range.startInclusive, range.endExclusive)
        RealAppUsage(
            packageName = packageName,
            name = appInfo?.loadLabel(packageManager)?.toString() ?: packageName,
            icon = appInfo?.loadIcon(packageManager),
            minutes = minutes,
            percentage = if (totalMillis > 0L) (totals.first.toDouble() / totalMillis).toFloat().coerceIn(0f, 1f) else 0f,
            lastUsedMillis = totals.second,
            launches = launchCounts[packageName] ?: 0,
            trackedReelsSeconds = if (hasReelsRecord) ProtectionPreferences.getRecordedUsageSecondsBetween(context, packageName, range.startInclusive, range.endExclusive) else null
        )
    }
    val chart = buildChart(manager, tab, today, nowMillis, zone, locale, ownPackage)
    val peak = chart.filter { it.minutes > 0 }.maxByOrNull { it.minutes }
    val previousStats = if (previousEnd > previousStart) manager.queryUsageStats(
        UsageStatsManager.INTERVAL_BEST, previousStart, previousEnd
    ).orEmpty() else emptyList()
    val previousMinutes = previousStats.filter { it.packageName != ownPackage }
        .sumOf { it.totalTimeInForeground }
        .takeIf { previousStats.isNotEmpty() }
        ?.div(60_000L)
        ?.coerceAtMost(Int.MAX_VALUE.toLong())
        ?.toInt()
    val hasData = totalMillis > 0L

    return RealStatistics(
        totalMinutes = totalMinutes,
        reelsSeconds = reelsSeconds,
        appUsage = topApps,
        chart = chart,
        peakRange = peak?.label,
        peakDescription = if (peak != null) "أعلى استخدام مسجل في هذه الفترة" else "لا توجد بيانات استخدام كافية لهذه الفترة.",
        previousMinutes = previousMinutes,
        hasPermission = true,
        hasData = hasData
    )
}

private fun Long?.orZero(): Long = this ?: 0L

private fun buildChart(
    manager: UsageStatsManager,
    tab: Int,
    today: LocalDate,
    nowMillis: Long,
    zone: ZoneId,
    locale: Locale,
    ownPackage: String
): List<RealChartPoint> {
    return usageChartBuckets(tab, today, locale).map { bucket ->
        val bucketStart = bucket.start.atZone(zone).toInstant().toEpochMilli()
        val bucketEnd = minOf(bucket.endExclusive.atZone(zone).toInstant().toEpochMilli(), nowMillis)
        val minutes = if (bucketStart >= bucketEnd) 0 else manager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST, bucketStart, bucketEnd
        ).orEmpty().asSequence()
            .filter { it.packageName != ownPackage }
            .sumOf { it.totalTimeInForeground }
            .div(60_000L)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()
        RealChartPoint(bucket.label, minutes)
    }
}

private fun hasUsagePermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun formatDuration(minutes: Int): String {
    if (minutes < 60) return "${toArabicDigits(minutes.toString())} دقيقة"
    return "${toArabicDigits((minutes / 60).toString())}س ${toArabicDigits((minutes % 60).toString())}د"
}

private fun formatDurationSeconds(seconds: Long): String {
    if (seconds < 60L) return "${toArabicDigits(seconds.coerceAtLeast(0L).toString())} ث"
    val minutes = seconds / 60L
    if (minutes < 60L) return "${toArabicDigits(minutes.toString())} دقيقة"
    return "${toArabicDigits((minutes / 60L).toString())}س ${toArabicDigits((minutes % 60L).toString())}د"
}

private fun usageTotalLabel(data: RealStatistics): String = when {
    data.isLoading -> "جارٍ التحميل"
    !data.hasPermission -> "غير متاح"
    !data.hasData -> "لا توجد بيانات"
    else -> formatDuration(data.totalMinutes)
}

private fun appCountLabel(data: RealStatistics): String = when {
    data.isLoading -> "جارٍ التحميل"
    !data.hasPermission -> "غير متاح"
    !data.hasData -> "لا توجد بيانات"
    else -> toArabicDigits(data.appUsage.size.toString())
}

private fun chartTitle(tab: Int): String = when (tab) {
    0 -> "ساعات الاستخدام اليومية"
    1 -> "ساعات الاستخدام الأسبوعية"
    else -> "ساعات الاستخدام الشهرية"
}

private fun periodLabel(tab: Int): String = when (tab) {
    0 -> "اليوم"
    1 -> "هذا الأسبوع"
    else -> "هذا الشهر"
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
