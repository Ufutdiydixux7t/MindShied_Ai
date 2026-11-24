package com.mindguard.feature.self

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(modifier = Modifier.fillMaxSize()) {
        HomeBottomWaveDecoration(modifier = Modifier.align(Alignment.BottomCenter))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            ManualProtectionStatusCard(
                enabled = protectionEnabled,
                protectedAppsCount = protectedApps.size
            )

            if (protectedApps.isEmpty()) {
                EmptyProtectionCard()
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = RoundedCornerShape(13.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(9.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = "التطبيقات المحمية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "الوقت المتبقي اليوم",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = protectedApps.size.toString(),
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                protectedApps.forEach { app ->
                    ProtectedAppRemainingCard(app)
                }
            }

            DailyAdviceCard(protectedApps)
            // Keep the final card clear of the floating bottom navigation bar.
            Spacer(modifier = Modifier.height(112.dp))
        }
    }
}

@Composable
private fun HomeBottomWaveDecoration(modifier: Modifier = Modifier) {
    val waveStart = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f)
    val waveEnd = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
    ) {
        val width = size.width
        val height = size.height
        val wave = Path().apply {
            moveTo(0f, height * 0.34f)
            cubicTo(
                width * 0.28f, height * 0.12f,
                width * 0.68f, height * 0.82f,
                width, height * 0.24f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = wave,
            brush = Brush.verticalGradient(
                colors = listOf(waveStart, waveEnd),
                startY = 0f,
                endY = height
            )
        )
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
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = if (enabled) "الحماية اليدوية مفعّلة" else "الحماية اليدوية غير مفعّلة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (enabled) "ملخص استخدام التطبيقات المحمية" else "أضف تطبيقات للحماية لبدء المتابعة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f),
                    modifier = Modifier.padding(top = 3.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = protectedAppsCount.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "تطبيقات محمية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
            ) {
                Text(
                    text = if (enabled) "نشطة" else "غير نشطة",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
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
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 17.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val periodText = if (app.periodName != null && app.periodStart != null && app.periodEnd != null) {
                        "${app.periodName} · ${app.periodStart} - ${app.periodEnd}"
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

            Spacer(modifier = Modifier.height(14.dp))

            if (app.isFrozen) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.62f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "التطبيق مجمّد حالياً",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            } else if (remainingSeconds != null && limitSeconds != null) {
                LinearProgressIndicator(
                    progress = { app.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                        color = MaterialTheme.colorScheme.primary
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
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "لا توجد تطبيقات محمية بعد",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "أضف التطبيقات من تبويب الحماية لتظهر متابعة استخدامها هنا.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 6.dp)
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.62f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
            ) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(11.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "نصيحة اليوم",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = advice,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
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
