package com.mindguard.feature.protection

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.mindguard.core.theme.Primary
import com.mindguard.core.theme.Success
import com.mindguard.core.theme.TextDark
import com.mindguard.core.theme.TextGray

@Composable
fun ProtectionScreen(
    viewModel: ProtectionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AnimatedContent(
            targetState = uiState.activeSubScreen,
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                } else {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                }
            },
            label = "ProtectionSubScreenTransition"
        ) { subScreen ->
            when (subScreen) {
                null -> MainProtectionContent(
                    onCardClick = { viewModel.navigateToSubScreen(it) }
                )
                ProtectionSubScreen.APP_PROTECTION -> AppProtectionSubScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ProtectionSubScreen.CONTENT_PROTECTION -> ContentProtectionSubScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ProtectionSubScreen.REST_AND_SLEEP -> RestAndSleepSubScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ProtectionSubScreen.SETTINGS_LOCK -> SettingsLockSubScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ProtectionSubScreen.ADVANCED_SETTINGS -> AdvancedSettingsSubScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
            }
        }
    }
}

@Composable
fun MainProtectionContent(
    onCardClick: (ProtectionSubScreen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top MindGuard Branding Header (Exact match to screenshot)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 16.dp)
        ) {
            // Brand Logo & Text at Top Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MindGuard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = Primary.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "MindGuard Logo",
                        tint = Primary,
                        modifier = Modifier.padding(7.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Large Heading: الحماية
            Text(
                text = "الحماية",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle: اختر ما تريد حمايته
            Text(
                text = "اختر ما تريد حمايته",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray,
                fontWeight = FontWeight.Normal
            )
        }

        // 5 Core Protection Menu Cards List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: حماية التطبيقات
            ProtectionImageCard(
                title = "حماية التطبيقات",
                subtitle = "حظر الريلز وتجميد التطبيقات عند الحاجة",
                icon = Icons.Outlined.GridView,
                onClick = { onCardClick(ProtectionSubScreen.APP_PROTECTION) }
            )

            // Card 2: حماية المحتوى غير المناسب
            ProtectionImageCard(
                title = "حماية المحتوى غير المناسب",
                subtitle = "تصفية المحتوى وحماية تجربة التصفح",
                icon = Icons.Outlined.Shield,
                onClick = { onCardClick(ProtectionSubScreen.CONTENT_PROTECTION) }
            )

            // Card 3: الراحة والنوم
            ProtectionImageCard(
                title = "الراحة والنوم",
                subtitle = "جدولة أوقات الراحة والنوم",
                icon = Icons.Outlined.Bedtime,
                onClick = { onCardClick(ProtectionSubScreen.REST_AND_SLEEP) }
            )

            // Card 4: قفل الإعدادات
            ProtectionImageCard(
                title = "قفل الإعدادات",
                subtitle = "حماية إعداداتك برمز آمن",
                icon = Icons.Outlined.Lock,
                onClick = { onCardClick(ProtectionSubScreen.SETTINGS_LOCK) }
            )

            // Card 5: الحماية المتقدمة
            ProtectionImageCard(
                title = "الحماية المتقدمة",
                subtitle = null, // Matches screenshot
                icon = Icons.Outlined.Settings,
                onClick = { onCardClick(ProtectionSubScreen.ADVANCED_SETTINGS) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProtectionImageCard(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right Light Blue Square Icon Container (Appears on the RIGHT in RTL)
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFEBF3FE) // Soft light blue tint
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF1E50B5), // Darker rich blue icon
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right-aligned Text Info (Appears in the MIDDLE in RTL)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = TextGray,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Left Chevron Arrow < (Appears on the LEFT in RTL)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ----------------------------------------------------------------------
// Sub-Screen 1: حماية التطبيقات (App Protection Sub-Screen matching screenshot)
// ----------------------------------------------------------------------
@Composable
fun AppProtectionSubScreen(
    uiState: ProtectionUiState,
    viewModel: ProtectionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showAddAppDialog by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Protection Screen Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { showAddAppDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة تطبيق", color = Primary, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "رجوع",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "حماية التطبيقات",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "حظر التطبيقات التي تستهلك وقتك",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                fontSize = 13.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section Title: التطبيقات
            Text(
                text = "التطبيقات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )

            if (uiState.appProtectionItems.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "لا توجد تطبيقات قابلة للتشغيل على الجهاز",
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        color = TextGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                uiState.appProtectionItems.forEach { item ->
                    ExpandableAppProtectionCard(
                        item = item,
                        onToggleExpand = { viewModel.toggleAppExpansion(item.id) },
                        onReelsToggle = { enabled ->
                            if (enabled && !viewModel.isAccessibilityServiceEnabled()) {
                                showAccessibilityDialog = true
                            } else {
                                viewModel.toggleAppReelsBlock(item.id, enabled)
                            }
                        },
                        onReelsDurationSelect = { viewModel.setAppReelsDuration(item.id, it) },
                        onFreezeToggle = { enabled ->
                            if (enabled && !viewModel.isAccessibilityServiceEnabled()) {
                                showAccessibilityDialog = true
                            } else {
                                viewModel.toggleAppFreeze(item.id, enabled)
                            }
                        },
                        onFreezeDurationSelect = { viewModel.setAppFreezeDuration(item.id, it) },
                        onPeriodMinutesChange = { periodId, minutes ->
                            viewModel.updateReelsPeriodMinutes(item.id, periodId, minutes)
                        },
                        onAddPeriod = { viewModel.addReelsPeriod(item.id) },
                        onPeriodsToggle = { viewModel.togglePeriodsVisibility(item.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddAppDialog) {
        AlertDialog(
            onDismissRequest = { showAddAppDialog = false },
            title = { Text("إضافة تطبيق للحماية") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val addedIds = uiState.appProtectionItems.map { it.id }.toSet()
                    uiState.availableApps.filterNot { it.id in addedIds }.forEach { app ->
                        TextButton(
                            onClick = {
                                viewModel.addApp(app.id)
                                showAddAppDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppBrandIconBadge(app.id, app.name, app.icon)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(app.name, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAddAppDialog = false }) { Text("إغلاق") } }
        )
    }

    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("تفعيل خدمة إمكانية الوصول") },
            text = { Text("يحتاج MindGuard إلى خدمة إمكانية الوصول لاكتشاف فتح التطبيق المحمي ومنع استخدامه في وقت الحظر.") },
            confirmButton = {
                Button(onClick = {
                    showAccessibilityDialog = false
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }) { Text("فتح الإعدادات") }
            },
            dismissButton = { TextButton(onClick = { showAccessibilityDialog = false }) { Text("لاحقًا") } }
        )
    }
}

@Composable
fun ExpandableAppProtectionCard(
    item: AppProtectionItemState,
    onToggleExpand: () -> Unit,
    onReelsToggle: (Boolean) -> Unit,
    onReelsDurationSelect: (String) -> Unit,
    onFreezeToggle: (Boolean) -> Unit,
    onFreezeDurationSelect: (String) -> Unit,
    onPeriodMinutesChange: (String, String) -> Unit,
    onAddPeriod: () -> Unit,
    onPeriodsToggle: () -> Unit
) {
    val durationOptions = listOf("5 دقائق", "10 دقائق", "15 دقيقة", "30 دقيقة", "45 دقيقة", "ساعة", "ساعتان")
    var reelsDropdownExpanded by remember { mutableStateOf(false) }
    var freezeDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBrandIconBadge(appId = item.id, appName = item.name, icon = item.icon)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0B1652),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (item.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF0B1652),
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(visible = item.isExpanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)) {
                    HorizontalDivider(color = Color(0xFFE7EAF0))
                    ProtectionSwitchRow(
                        label = "حظر الريلز",
                        checked = item.isReelsBlocked,
                        onCheckedChange = onReelsToggle
                    )
                    if (item.isReelsBlocked) {
                        CompactDurationSelector(
                            value = item.reelsBlockDuration,
                            expanded = reelsDropdownExpanded,
                            onExpand = { reelsDropdownExpanded = true },
                            onDismiss = { reelsDropdownExpanded = false },
                            options = durationOptions,
                            onSelect = {
                                onReelsDurationSelect(it)
                                reelsDropdownExpanded = false
                            }
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE7EAF0))
                    ProtectionSwitchRow(
                        label = "الفترات",
                        checked = item.arePeriodsVisible,
                        onCheckedChange = { onPeriodsToggle() }
                    )
                    if (item.arePeriodsVisible) {
                        Text(
                            text = "حدد وقت المشاهدة لكل فترة يومية",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        item.reelsPeriods.forEach { period ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC)),
                                shape = RoundedCornerShape(14.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(period.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                                        Text("${period.startTime} - ${period.endTime}", fontSize = 11.sp, color = TextGray)
                                    }
                                    OutlinedTextField(
                                        value = period.minutes,
                                        onValueChange = { onPeriodMinutesChange(period.id, it) },
                                        modifier = Modifier.width(88.dp),
                                        singleLine = true,
                                        label = { Text("دقائق", fontSize = 10.sp) },
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                        TextButton(onClick = onAddPeriod) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة فترة", fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE7EAF0))
                    ProtectionSwitchRow(
                        label = "تجميد التطبيق",
                        checked = item.isAppFrozen,
                        onCheckedChange = onFreezeToggle
                    )
                    if (item.isAppFrozen) {
                        CompactDurationSelector(
                            value = item.appFreezeDuration,
                            expanded = freezeDropdownExpanded,
                            onExpand = { freezeDropdownExpanded = true },
                            onDismiss = { freezeDropdownExpanded = false },
                            options = durationOptions,
                            onSelect = {
                                onFreezeDurationSelect(it)
                                freezeDropdownExpanded = false
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun ProtectionSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0B1652),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary,
                uncheckedThumbColor = Color(0xFF64748B),
                uncheckedTrackColor = Color(0xFFE2E8F0),
                uncheckedBorderColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Composable
private fun CompactDurationSelector(
    value: String,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        OutlinedButton(
            onClick = onExpand,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFD5DAE4)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
        ) {
            Text("المدة: $value", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start, color = TextDark)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextGray)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 13.sp) },
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

@Composable
fun AppBrandIconBadge(appId: String, appName: String, icon: android.graphics.drawable.Drawable?) {
    val (bgColor, iconVector) = when (appId.lowercase()) {
        "tiktok" -> Color(0xFF000000) to Icons.Outlined.MusicNote
        "instagram" -> Color(0xFFE1306C) to Icons.Outlined.CameraAlt
        "youtube" -> Color(0xFFFF0000) to Icons.Outlined.PlayArrow
        "snapchat" -> Color(0xFFFFC107) to Icons.Outlined.Camera
        else -> Primary to Icons.Outlined.Apps
    }

    Surface(
        modifier = Modifier.size(36.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (icon == null) bgColor else Color.Transparent
    ) {
        if (icon != null) {
            Image(
                painter = rememberAsyncImagePainter(icon),
                contentDescription = appName,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = iconVector,
                contentDescription = appName,
                tint = if (appId.lowercase() == "snapchat") Color.Black else Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// ----------------------------------------------------------------------
// Sub-Screen 2: حماية المحتوى غير المناسب (Content Protection Sub-Screen)
// ----------------------------------------------------------------------
@Composable
fun ContentProtectionSubScreen(
    uiState: ProtectionUiState,
    viewModel: ProtectionViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SubScreenHeader(title = "حماية المحتوى غير المناسب", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubScreenSectionTitle("تصفية الذكاء الاصطناعي")

            SubScreenToggleCard(
                title = "فحص الكلمات والمشاهد الحساسة",
                description = "اكتشاف المحتوى الضار وحجبه فوراً باستخدام AI",
                icon = Icons.Outlined.AutoAwesome,
                checked = uiState.isAiFilteringActive,
                onCheckedChange = { viewModel.toggleAiFiltering(it) }
            )

            SubScreenToggleCard(
                title = "حظر المواقع والمحتوى الإباحي",
                description = "حجب كافة المواقع غير اللائقة بشكل نهائي",
                icon = Icons.Outlined.Block,
                checked = uiState.isAdultContentBlocked,
                onCheckedChange = { viewModel.toggleAdultContent(it) }
            )

            SubScreenToggleCard(
                title = "التصفح الآمن وحظر الروابط المشبوهة",
                description = "حماية الجهاز من صفحات التصيد والاحتيال الإلكتروني",
                icon = Icons.Outlined.GppGood,
                checked = uiState.isSafeBrowsingActive,
                onCheckedChange = { viewModel.toggleSafeBrowsing(it) }
            )

            SubScreenSectionTitle("مستوى قوة التصفية")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("اختر درجة شدة التصفية الحالية:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val levels = listOf(1 to "خفيف", 2 to "متوسط", 3 to "صارم")
                        levels.forEach { (level, label) ->
                            val isSelected = uiState.filterStrictnessLevel == level
                            Button(
                                onClick = { viewModel.updateStrictnessLevel(level) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Primary else Color(0xFFF1F5F9),
                                    contentColor = if (isSelected) Color.White else TextDark
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(label, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------------------------
// Sub-Screen 3: الراحة والنوم (Rest & Sleep Sub-Screen)
// ----------------------------------------------------------------------
@Composable
fun RestAndSleepSubScreen(
    uiState: ProtectionUiState,
    viewModel: ProtectionViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SubScreenHeader(title = "الراحة والنوم", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubScreenSectionTitle("جدول الراحة والنوم")

            SubScreenToggleCard(
                title = "تفعيل الجدول اليومي للنوم",
                description = "إيقاف التشتيت وقفل التطبيقات ليلاً تلقائياً",
                icon = Icons.Outlined.Bedtime,
                checked = uiState.isSleepScheduleActive,
                onCheckedChange = { viewModel.toggleSleepSchedule(it) }
            )

            SubScreenSectionTitle("مواعيد النوم والاستيقاظ")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.NightsStay, contentDescription = null, tint = Primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("وقت النوم", style = MaterialTheme.typography.bodySmall, color = TextGray)
                        Text(uiState.bedtimeStart, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Primary)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("وقت الاستيقاظ", style = MaterialTheme.typography.bodySmall, color = TextGray)
                        Text(uiState.bedtimeEnd, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                    }
                }
            }

            SubScreenSectionTitle("إجراءات وقت النوم")

            SubScreenToggleCard(
                title = "قفل الشاشة أثناء فترة النوم",
                description = "منع فتح كافة التطبيقات المشتتة حتى موعد الاستيقاظ",
                icon = Icons.Outlined.Lock,
                checked = uiState.lockScreenDuringSleep,
                onCheckedChange = { viewModel.toggleLockScreenDuringSleep(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------------------------
// Sub-Screen 4: قفل الإعدادات (Settings Lock Sub-Screen)
// ----------------------------------------------------------------------
@Composable
fun SettingsLockSubScreen(
    uiState: ProtectionUiState,
    viewModel: ProtectionViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SubScreenHeader(title = "قفل الإعدادات", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubScreenSectionTitle("حماية الإعدادات برمز PIN")

            SubScreenToggleCard(
                title = "تفعيل رمز PIN الآمن",
                description = "طلب رمز السر عند فتح التطبيق أو تغيير خيارات الحماية",
                icon = Icons.Outlined.Lock,
                checked = uiState.isSettingsLockActive,
                onCheckedChange = { viewModel.toggleSettingsLock(it) }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("رمز PIN الحالي", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("•••• (${uiState.pinCode})", style = MaterialTheme.typography.bodySmall, color = TextGray)
                    }
                    Button(
                        onClick = { viewModel.updatePinCode("4321") },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary.copy(alpha = 0.1f), contentColor = Primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تغيير الرمز", fontWeight = FontWeight.Bold)
                    }
                }
            }

            SubScreenSectionTitle("طرق الأمان الإضافية")

            SubScreenToggleCard(
                title = "تفعيل البصمة (Biometrics)",
                description = "السماح بإلغاء القفل باستخدام بصمة الإصبع أو الوجه",
                icon = Icons.Outlined.Fingerprint,
                checked = uiState.isBiometricsActive,
                onCheckedChange = { viewModel.toggleBiometrics(it) }
            )

            SubScreenToggleCard(
                title = "قفل تعديل تاريخ ووقت النظام",
                description = "منع التحايل عبر تغيير الوقت لتجاوز حدود الاستخدام",
                icon = Icons.Outlined.AccessTime,
                checked = uiState.lockTimeAndDateChange,
                onCheckedChange = { viewModel.toggleLockTimeAndDate(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------------------------
// Sub-Screen 5: الحماية المتقدمة (Advanced Protection Sub-Screen matching screenshot)
// ----------------------------------------------------------------------
@Composable
fun AdvancedSettingsSubScreen(
    uiState: ProtectionUiState,
    viewModel: ProtectionViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 20.dp)
    ) {
        // Sub-screen Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "رجوع",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title: تعديل مدة الحظر
            Text(
                text = "تعديل مدة الحظر",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "اختر طريقة تعديل المدة",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: مسموح تعديل المدة
            val isOption1Selected = uiState.blockDurationEditMode == BlockDurationEditMode.ALLOWED_NOW
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setBlockDurationEditMode(BlockDurationEditMode.ALLOWED_NOW) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isOption1Selected) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isOption1Selected) 1.5.dp else 1.dp,
                    color = if (isOption1Selected) Color(0xFF1E50B5) else Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = Color(0xFFDBEAFE)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF1E50B5),
                            modifier = Modifier.padding(11.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مسموح تعديل المدة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "يمكنك تعديل مدة الحظر الآن",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }

                    RadioButton(
                        selected = isOption1Selected,
                        onClick = { viewModel.setBlockDurationEditMode(BlockDurationEditMode.ALLOWED_NOW) },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1E50B5))
                    )
                }
            }

            // Option 2: تعديل المدة بعد يومين
            val isOption2Selected = uiState.blockDurationEditMode == BlockDurationEditMode.ALLOW_AFTER_TWO_DAYS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setBlockDurationEditMode(BlockDurationEditMode.ALLOW_AFTER_TWO_DAYS) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isOption2Selected) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isOption2Selected) 1.5.dp else 1.dp,
                    color = if (isOption2Selected) Color(0xFF1E50B5) else Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = Color(0xFFEFF6FF)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Color(0xFF1E50B5),
                            modifier = Modifier.padding(11.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تعديل المدة بعد يومين",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "لا يمكنك تعديل المدة إلا بعد يومين، وسيطبق هذا الخيار على تطبيقاتك.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = TextGray,
                            lineHeight = 18.sp
                        )
                    }

                    RadioButton(
                        selected = isOption2Selected,
                        onClick = { viewModel.setBlockDurationEditMode(BlockDurationEditMode.ALLOW_AFTER_TWO_DAYS) },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1E50B5))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prevent Uninstall Section (منع حذف التطبيق)
            SubScreenToggleCard(
                title = "منع حذف التطبيق",
                description = "حماية التطبيق من الحذف بدون إذن مسؤول النظام",
                icon = Icons.Outlined.AdminPanelSettings,
                checked = uiState.isPreventUninstallActive,
                onCheckedChange = { viewModel.togglePreventUninstall(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Action Button: حفظ الإعدادات
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B365D)),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = "حفظ الإعدادات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

// ----------------------------------------------------------------------
// Common Helper Reusable Composables for Sub-Screens
// ----------------------------------------------------------------------
@Composable
fun SubScreenHeader(
    title: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "رجوع",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SubScreenSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun SubScreenToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEBF3FE)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF1E50B5),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = TextGray,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary
                )
            )
        }
    }
}

@Composable
fun PermissionStatusRow(
    title: String,
    isGranted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isGranted) Success.copy(alpha = 0.12f) else Color.Red.copy(alpha = 0.12f)
        ) {
            Text(
                text = if (isGranted) "مفعلة" else "غير مفعلة",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isGranted) Success else Color.Red,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
