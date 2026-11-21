package com.mindguard.feature.self

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguard.core.theme.Primary
import com.mindguard.platform.accessibility.ProtectionPreferences
import java.time.LocalDate
import kotlinx.coroutines.delay

data class HomeProtectedAppState(
    val packageName: String,
    val appName: String,
    val isReelsEnabled: Boolean,
    val isPeriodsEnabled: Boolean,
    val isFrozen: Boolean,
    val periodName: String?,
    val periodStart: String?,
    val periodEnd: String?,
    val usedSeconds: Int,
    val limitSeconds: Int?
) {
    val remainingSeconds: Int?
        get() = limitSeconds?.minus(usedSeconds)?.coerceAtLeast(0)

    val progress: Float
        get() = if (limitSeconds == null || limitSeconds <= 0) {
            0f
        } else {
            (usedSeconds.toFloat() / limitSeconds.toFloat()).coerceIn(0f, 1f)
        }
}

@Composable
fun SelfHomeScreen() {
    val context = LocalContext.current
    var refreshTick by remember { mutableIntStateOf(0) }
    val protectedApps = remember(context, refreshTick) {
        loadProtectedApps(context)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5_000L)
            refreshTick++
        }
    }

    val protectionEnabled = protectedApps.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        ManualProtectionStatusCard(
            enabled = protectionEnabled,
            protectedAppsCount = protectedApps.size
        )

        if (protectedApps.isEmpty()) {
            EmptyProtectionCard()
        } else {
            Text(
                text = "الوقت المتبقي",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )

            protectedApps.forEach { app ->
                ProtectedAppRemainingCard(app)
            }
        }

        DailyAdviceCard(protectedApps)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ManualProtectionStatusCard(
    enabled: Boolean,
    protectedAppsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFFEAF3FF) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (enabled) Primary.copy(alpha = 0.28f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (enabled) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                tint = if (enabled) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 10.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (enabled) "الحماية اليدوية مفعّلة" else "الحماية اليدوية غير مفعّلة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (enabled) "$protectedAppsCount تطبيقات محمية" else "لم تتم إضافة تطبيقات للحماية بعد",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun ProtectedAppRemainingCard(app: HomeProtectedAppState) {
    val remainingSeconds = app.remainingSeconds
    val limitSeconds = app.limitSeconds

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (app.isFrozen) Icons.Default.Security else Icons.Default.HourglassBottom,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val periodText = if (app.periodName != null && app.periodStart != null && app.periodEnd != null) {
                        "${app.periodName} • ${app.periodStart} - ${app.periodEnd}"
                    } else {
                        "الحد اليومي"
                    }
                    Text(
                        text = periodText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (app.isFrozen) {
                Text(
                    text = "التطبيق مجمّد حالياً",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            } else if (remainingSeconds != null && limitSeconds != null) {
                LinearProgressIndicator(
                    progress = { app.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "المستخدم ${formatMinutes(app.usedSeconds)} من ${formatMinutes(limitSeconds)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "متبقي ${formatMinutes(remainingSeconds)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            } else {
                Text(
                    text = "لا توجد فترة مشاهدة حالية",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

@Composable
private fun EmptyProtectionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SelfImprovement,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "لا توجد أوقات مشاهدة مسجلة اليوم",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DailyAdviceCard(apps: List<HomeProtectedAppState>) {
    val advice = when {
        apps.any { app -> app.remainingSeconds?.let { it <= 60 } == true } ->
            "اقترب وقت المشاهدة من الانتهاء. خذ استراحة قصيرة قبل الانتقال إلى تطبيق آخر."
        apps.any { it.usedSeconds >= 15 * 60 } ->
            "الاستخدام المتواصل للمقاطع القصيرة قد تؤثر في التركيز والنوم. حاول أخذ فواصل قصيرة."
        apps.isNotEmpty() ->
            "حافظ على استخدام متوازن للمقاطع القصيرة، واجعل وقتك اليومي أكثر وعياً."
        else ->
            "ابدأ بخطوة بسيطة: حدد وقتاً واضحاً للمشاهدة وتجنب الاستخدام المتواصل."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3FF)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.SelfImprovement,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.padding(start = 10.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "نصيحة اليوم",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    text = advice,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

private fun loadProtectedApps(context: Context): List<HomeProtectedAppState> {
    val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val packageManager = context.packageManager
    val ownPackage = context.packageName

    return packageManager.queryIntentActivities(launcherIntent, 0)
        .asSequence()
        .map { it.activityInfo.applicationInfo }
        .filter { it.packageName != ownPackage }
        .distinctBy { it.packageName }
        .mapNotNull { appInfo ->
            val packageName = appInfo.packageName
            val reelsEnabled = ProtectionPreferences.isReelsEnabled(context, packageName)
            val periodsEnabled = ProtectionPreferences.arePeriodsEnabled(context, packageName)
            val frozen = ProtectionPreferences.isFrozen(context, packageName)
            if (!reelsEnabled && !periodsEnabled && !frozen) return@mapNotNull null

            val period = if (periodsEnabled) {
                ProtectionPreferences.currentPeriod(context, packageName)
            } else {
                null
            }
            val periodId = period?.id ?: "global"
            val usedSeconds = ProtectionPreferences.getTotalUsedSeconds(
                context,
                packageName,
                LocalDate.now().toString(),
                periodId
            )
            val limitMinutes = if (reelsEnabled) {
                period?.minutes ?: ProtectionPreferences.getReelsLimitMinutes(context, packageName)
            } else {
                null
            }

            HomeProtectedAppState(
                packageName = packageName,
                appName = appInfo.loadLabel(packageManager).toString(),
                isReelsEnabled = reelsEnabled,
                isPeriodsEnabled = periodsEnabled,
                isFrozen = frozen,
                periodName = period?.id?.let(::periodDisplayName),
                periodStart = period?.start,
                periodEnd = period?.end,
                usedSeconds = usedSeconds,
                limitSeconds = limitMinutes?.coerceAtLeast(0)?.times(60)
            )
        }
        .sortedBy { it.appName.lowercase() }
        .toList()
}

private fun periodDisplayName(id: String): String = when (id) {
    "morning" -> "الصباح"
    "afternoon" -> "الظهر"
    "evening" -> "المساء"
    else -> "الفترة الحالية"
}

private fun formatMinutes(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes == 0) {
        "$remainingSeconds ثانية"
    } else if (remainingSeconds == 0) {
        "$minutes دقيقة"
    } else {
        "$minutes دقيقة و$remainingSeconds ثانية"
    }
}
