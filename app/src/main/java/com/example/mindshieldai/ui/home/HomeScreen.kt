package com.example.mindshieldai.ui.home

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import coil.compose.AsyncImage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindshieldai.ui.theme.DarkBlue
import com.example.mindshieldai.ui.theme.Primary
import com.example.mindshieldai.ui.theme.TextDark

enum class SettingsSubScreen {
    Main, Account, Notifications, Privacy, Security
}

@Composable
fun HomeScreen(
    userName: String = "أحمد",
    navController: NavController? = null,
    isDarkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    profileImageUri: Uri? = null
) {
    var currentUserName by remember { mutableStateOf(userName) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val isHome = selectedTab == 0

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            },
            containerColor = if (isHome) DarkBlue else MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isHome) {
                    HomeTopBar(currentUserName, profileImageUri)
                }

                // Content Area
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = if (isHome) RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp) else RoundedCornerShape(0.dp)
                ) {
                    when (selectedTab) {
                        0 -> MainContent()
                        1 -> StatsContent()
                        2 -> ProtectionContent()
                        3 -> MonitoringContent()
                        4 -> MoreContent(
                            isDarkMode = isDarkMode,
                            onLogout = {
                                navController?.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToTab = { selectedTab = it },
                            navController = navController,
                            profileImageUri = profileImageUri
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    val uiState = remember { DashboardUiState() }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 8. التنبيه المهم (Conditional)
        uiState.importantAlert?.let { alert ->
            ImportantAlertCard(alert)
        }

        // 1. بطاقة "وقت الريلز اليوم"
        DailyReelsCard(
            used = uiState.reelsUsedMinutes,
            limit = uiState.reelsLimitMinutes
        )

        // 2. بطاقة "الفترة الحالية"
        CurrentPeriodCard(uiState.currentPeriod)

        // 3. بطاقة "حالة الحماية"
        ProtectionStatusCard(uiState.protectionEnabled)

        // 4. قسم "التطبيقات المحمية"
        ProtectedAppsSection(uiState.protectedApps)

        // 5. قسم "نشاط اليوم"
        TodayActivitySection(uiState.activityStats)

        // 6. بطاقة "نصيحة MindShield AI"
        AiTipCard(uiState.aiTip)

        // 7. قسم "إنجازات اليوم"
        AchievementsSection(uiState.achievements)
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ImportantAlertCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚠️", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DailyReelsCard(used: Int, limit: Int) {
    val remaining = (limit - used).coerceAtLeast(0)
    val progress = used.toFloat() / limit.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "وقت الريلز اليوم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$used دقيقة مستخدمة",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDark,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$remaining دقيقة متبقية",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                // Background Circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFFF1F5F9),
                        style = Stroke(width = 8.dp.toPx())
                    )
                }
                // Progress Circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Primary,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
            }
        }
    }
}

@Composable
fun CurrentPeriodCard(period: CurrentPeriodInfo) {
    val progress = period.usedMinutes.toFloat() / period.totalMinutes.toFloat()
    val remaining = (period.totalMinutes - period.usedMinutes).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الفترة الحالية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
                Text(
                    text = "${period.startTime} - ${period.endTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Primary,
                trackColor = Color(0xFFF1F5F9)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "استخدمت ${period.usedMinutes} من ${period.totalMinutes} دقيقة",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark
                )
                Text(
                    text = "متبقي $remaining دقائق",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "الفترة القادمة تبدأ في ${period.nextPeriodStartTime}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ProtectionStatusCard(isEnabled: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isEnabled) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ) {
                Icon(
                    imageVector = if (isEnabled) Icons.Default.Shield else Icons.Default.ShieldMoon,
                    contentDescription = null,
                    tint = if (isEnabled) Color(0xFF43A047) else Color(0xFFD32F2F),
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isEnabled) "الحماية مفعلة" else "الحماية متوقفة",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
                Text(
                    text = "جميع التطبيقات المحددة تحت الحماية",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            TextButton(onClick = { /* Navigate to protection settings */ }) {
                Text("إدارة الحماية", style = MaterialTheme.typography.labelLarge, color = Primary)
            }
        }
    }
}

@Composable
fun ProtectedAppsSection(apps: List<AppUsageInfo>) {
    Column {
        Text(
            text = "التطبيقات المحمية",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBlue,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            apps.take(2).forEach { app ->
                AppCard(app, Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            apps.drop(2).take(2).forEach { app ->
                AppCard(app, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AppCard(app: AppUsageInfo, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9)
                ) {
                    // Placeholder for app icon
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        tint = DarkBlue
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "الاستخدام: ${app.usageTime}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF43A047),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "محمي",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF43A047),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TodayActivitySection(stats: DailyActivityStats) {
    Column {
        Text(
            text = "نشاط اليوم",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBlue,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActivityStatCard("${stats.sessions} جلسات", Modifier.weight(1f))
            ActivityStatCard("${stats.usageTimeMinutes} دقيقة استخدام", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActivityStatCard("${stats.videosCount} فيديو", Modifier.weight(1f))
            ActivityStatCard("${stats.mostUsedApp} الأكثر استخدامًا", Modifier.weight(1f))
        }
    }
}

@Composable
fun ActivityStatCard(label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Box(
            modifier = Modifier.padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = DarkBlue,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AiTipCard(tip: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "نصيحة MindShield AI",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "نصيحة اليوم",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AchievementsSection(achievements: List<Achievement>) {
    Column {
        Text(
            text = "إنجازات اليوم",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBlue,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                achievements.forEach { achievement ->
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (achievement.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (achievement.isCompleted) Color(0xFF43A047) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = achievement.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (achievement.isCompleted) TextDark else Color.Gray,
                            fontWeight = if (achievement.isCompleted) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("الإحصائيات", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Text("هنا ستظهر تقارير استخدامك اليومية والأسبوعية.", textAlign = TextAlign.Center)
    }
}

@Composable
fun ProtectionContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "الحماية",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        SettingsItem(
            title = "التطبيقات المحمية",
            description = "إدارة التطبيقات التي يحميها MindShield AI.",
            icon = Icons.Default.Apps,
            iconContainerColor = Color(0xFFE8F5E9),
            iconColor = Color(0xFF43A047),
            onClick = { /* Handle click */ }
        )
        
        SettingsItem(
            title = "مستوى الحماية الذكي",
            description = "ضبط حساسية الذكاء الاصطناعي في الحماية.",
            icon = Icons.Default.Psychology,
            iconContainerColor = Color(0xFFFFF3E0),
            iconColor = Color(0xFFFB8C00),
            onClick = { /* Handle click */ }
        )
    }
}

@Composable
fun MonitoringContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("الرقابة الأبوية", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Text("إدارة الأجهزة الملحقة ووضع الحدود الزمنية.", textAlign = TextAlign.Center)
    }
}

@Composable
fun MoreContent(
    isDarkMode: Boolean,
    onLogout: () -> Unit,
    onNavigateToTab: (Int) -> Unit,
    navController: NavController? = null,
    profileImageUri: Uri? = null
) {
    var subScreen by remember { mutableStateOf(SettingsSubScreen.Main) }

    when (subScreen) {
        SettingsSubScreen.Main -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    // MindShield AI Logo and Text (Centered)
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (isDarkMode) Color.White else DarkBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MindShield AI",
                            color = if (isDarkMode) Color.White else DarkBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    // Settings Title and Back Arrow (On the Right)
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الإعدادات",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else DarkBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = if (isDarkMode) Color.White else DarkBlue,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { /* Go back */ }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    // Account Section
                    Text(
                        text = "الحساب",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else DarkBlue,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    ProfileCard(
                        name = "أحمد محمد",
                        email = "ahmed.mohamed@example.com",
                        isDarkMode = isDarkMode,
                        onClick = { navController?.navigate("account_profile") },
                        profileImageUri = profileImageUri
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Settings Section
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else DarkBlue,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            SettingsItemNew(
                                title = "الإعدادات العامة",
                                icon = Icons.Outlined.Settings,
                                isDarkMode = isDarkMode,
                                onClick = { navController?.navigate("general_settings") }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))
                            SettingsItemNew(
                                title = "الحماية",
                                icon = Icons.Outlined.Shield,
                                isDarkMode = isDarkMode,
                                onClick = { onNavigateToTab(2) }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))
                            SettingsItemNew(
                                title = "التطبيقات",
                                icon = Icons.Outlined.GridView,
                                isDarkMode = isDarkMode,
                                onClick = { /* Handle Applications */ }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))
                            SettingsItemNew(
                                title = "الإشعارات",
                                icon = Icons.Outlined.Notifications,
                                isDarkMode = isDarkMode,
                                onClick = { subScreen = SettingsSubScreen.Notifications }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))
                            SettingsItemNew(
                                title = "الخصوصية",
                                icon = Icons.Outlined.Lock,
                                isDarkMode = isDarkMode,
                                onClick = { subScreen = SettingsSubScreen.Privacy }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))
                            SettingsItemNew(
                                title = "الأمان و PIN",
                                icon = Icons.Outlined.Shield,
                                isDarkMode = isDarkMode,
                                onClick = { subScreen = SettingsSubScreen.Security }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))
                            SettingsItemNew(
                                title = "الرقابة الأبوية",
                                icon = Icons.Outlined.Group,
                                isDarkMode = isDarkMode,
                                onClick = { onNavigateToTab(3) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Logout Button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        onClick = onLogout,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "تسجيل الخروج",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
        SettingsSubScreen.Account -> {
            // This is now handled by independent AccountProfileScreen
            // But keeping it for backward compatibility if needed, 
            // though the ProfileCard now navigates away.
            navController?.navigate("account_profile")
            subScreen = SettingsSubScreen.Main
        }
        SettingsSubScreen.Notifications -> {
            PlaceholderSettingsScreen(
                title = "الإشعارات",
                onBack = { subScreen = SettingsSubScreen.Main }
            )
        }
        SettingsSubScreen.Privacy -> {
            PlaceholderSettingsScreen(
                title = "الخصوصية",
                onBack = { subScreen = SettingsSubScreen.Main }
            )
        }
        SettingsSubScreen.Security -> {
            SecuritySettingsScreen(
                onBack = { subScreen = SettingsSubScreen.Main }
            )
        }
    }
}

@Composable
fun SecuritySettingsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SettingsHeader(title = "الأمان", onBack = onBack)
        
        Column(modifier = Modifier.padding(top = 16.dp)) {
            SettingsItem(
                title = "رمز الـ PIN",
                description = "إعداد أو تغيير رمز الـ PIN الخاص بك.",
                icon = Icons.Default.Dialpad,
                iconContainerColor = Color(0xFFFFF3E0),
                iconColor = Color(0xFFFB8C00),
                onClick = { /* TODO */ }
            )
            SettingsItem(
                title = "بصمة الإصبع",
                description = "تفعيل الدخول عبر البصمة.",
                icon = Icons.Default.Fingerprint,
                iconContainerColor = Color(0xFFFFF3E0),
                iconColor = Color(0xFFFB8C00),
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
fun PlaceholderSettingsScreen(title: String, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SettingsHeader(title = title, onBack = onBack)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("واجهة $title ستكون متاحة قريباً", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ProfileCard(
    name: String,
    email: String,
    isDarkMode: Boolean = false,
    profileImageUri: Uri? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomStart) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = if (isDarkMode) Color.DarkGray else Color(0xFFE2E8F0)
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                
                Surface(
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = 2.dp, y = (-2).dp),
                    shape = CircleShape,
                    color = if (isDarkMode) Primary else DarkBlue,
                    border = BorderStroke(1.5.dp, Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.Start, // Start is Right in RTL
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) Color.LightGray else Color.Gray
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = if (isDarkMode) Color.White else DarkBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SettingsItemNew(
    title: String,
    icon: ImageVector,
    isDarkMode: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDarkMode) Color.White else DarkBlue,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = null,
            tint = if (isDarkMode) Color.White else DarkBlue,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Back")
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(80.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (MaterialTheme.colorScheme.surface == Color.White) iconContainerColor else Color.DarkGray
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (MaterialTheme.colorScheme.surface == Color.White) iconColor else Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun HomeTopBar(userName: String, profileImageUri: Uri? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Right side: Greeting
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "مرحباً، $userName 👋",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "حماية ذكية لعقل أكثر صفاءً",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        // Left side: Logo and Notification
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Shield and Text
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MindShield AI",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            // Notification Bell (Extreme Left in RTL)
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notifications",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = Primary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
            label = { Text("الرئيسية") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Primary,
                unselectedTextColor = Color.Gray,
                indicatorColor = Primary.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = "الإحصائيات") },
            label = { Text("الإحصائيات") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Shield, contentDescription = "الحماية") },
            label = { Text("الحماية") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Group, contentDescription = "الرقابة") },
            label = { Text("الرقابة") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Menu, contentDescription = "المزيد") },
            label = { Text("المزيد") },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) }
        )
    }
}
