package com.mindguard.feature.protection

import android.app.TimePickerDialog
import android.content.Intent
import android.provider.Settings
import java.util.Locale
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.mindguard.core.components.AppTopBar
import com.mindguard.core.theme.Primary
import com.mindguard.core.theme.Success
import com.mindguard.core.theme.TextDark
import com.mindguard.core.theme.TextGray

@Composable
fun ProtectionScreen(
    viewModel: ProtectionViewModel = viewModel(),
    onSubScreenVisibilityChange: (Boolean) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(enabled = uiState.activeSubScreen != null) {
        viewModel.navigateBack()
    }

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps()
    }

    LaunchedEffect(uiState.activeSubScreen) {
        onSubScreenVisibilityChange(uiState.activeSubScreen == null)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.collapseAllApps()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.activeSubScreen == null) {
                AppTopBar(title = "الحماية")
            }
            when (val subScreen = uiState.activeSubScreen) {
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

            uiState.advancedProtectionMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = { viewModel.dismissAdvancedProtectionMessage() },
                    title = { Text("الحماية المتقدمة") },
                    text = {
                        Text(
                            text = message,
                            textAlign = TextAlign.Right,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.dismissAdvancedProtectionMessage() }) {
                            Text("حسنًا")
                        }
                    }
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
                imageVector = Icons.Default.KeyboardArrowLeft,
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
    var showUsageStatsDialog by remember { mutableStateOf(false) }
    var selectedAppIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var periodDialogAppId by remember { mutableStateOf<String?>(null) }
    var periodStartTime by remember { mutableStateOf("08:00") }
    var periodEndTime by remember { mutableStateOf("12:00") }
    var expandedFreezeAppId by remember { mutableStateOf<String?>(null) }
    var rootWindowOffset by remember { mutableStateOf(Offset.Zero) }
    var appCardBounds by remember { mutableStateOf<Map<String, Rect>>(emptyMap()) }

    fun formatTime(hour: Int, minute: Int): String =
        String.format(Locale.US, "%02d:%02d", hour, minute)

    fun choosePeriodTime(isStart: Boolean) {
        val initial = (if (isStart) periodStartTime else periodEndTime).split(":")
        val dialog = TimePickerDialog(context, { _, hour, minute ->
            val formatted = formatTime(hour, minute)
            if (isStart) periodStartTime = formatted else periodEndTime = formatted
        }, initial[0].toInt(), initial[1].toInt(), true)
        dialog.setTitle(if (isStart) "وقت بداية الفترة" else "وقت نهاية الفترة")
        dialog.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned { coordinates ->
                rootWindowOffset = coordinates.localToWindow(Offset.Zero)
            }
            .pointerInput(selectedAppIds, rootWindowOffset, appCardBounds) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Final)
                        val change = event.changes.firstOrNull()
                        val windowPoint = change?.position?.let {
                            Offset(rootWindowOffset.x + it.x, rootWindowOffset.y + it.y)
                        }
                        val tappedInsideCard = windowPoint != null && appCardBounds.values.any { it.contains(windowPoint) }
                        if (change?.changedToUp() == true && !change.isConsumed && !tappedInsideCard) {
                            selectedAppIds = emptySet()
                            expandedFreezeAppId = null
                            viewModel.collapseAllApps()
                        }
                    }
                }
            }
    ) {
        AppTopBar(title = "حماية التطبيقات", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
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
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section Title: التطبيقات
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "التطبيقات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = {
                        if (selectedAppIds.isEmpty()) {
                            showAddAppDialog = true
                        } else {
                            selectedAppIds.forEach(viewModel::removeApp)
                            selectedAppIds = emptySet()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (selectedAppIds.isEmpty()) Icons.Default.Add else Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = if (selectedAppIds.isEmpty()) Primary else Color(0xFFCC3D55),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (selectedAppIds.isEmpty()) "إضافة تطبيق" else "إزالة التطبيق",
                        color = if (selectedAppIds.isEmpty()) Primary else Color(0xFFCC3D55),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

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
                        onToggleExpand = {
                            selectedAppIds = emptySet()
                            expandedFreezeAppId = null
                            viewModel.toggleAppExpansion(item.id)
                        },
                        isSelected = item.id in selectedAppIds,
                        onCardBoundsChanged = { bounds ->
                            appCardBounds = appCardBounds + (item.id to bounds)
                        },
                        onLongPress = { selectedAppIds = selectedAppIds + item.id },
                        onReelsToggle = { enabled ->
                            if (enabled || viewModel.canEditBlockDuration()) {
                                if (enabled && !viewModel.isAccessibilityServiceEnabled()) {
                                    showAccessibilityDialog = true
                                } else {
                                    viewModel.toggleAppReelsBlock(item.id, enabled)
                                    if (enabled && !viewModel.isUsageStatsAccessEnabled()) {
                                        showUsageStatsDialog = true
                                    }
                                }
                            }
                        },
                        onReelsDurationSelect = {
                            if (viewModel.canEditBlockDuration()) viewModel.setAppReelsDuration(item.id, it)
                        },
                        onReelsMinutesChange = {
                            if (viewModel.canEditBlockDuration()) viewModel.setAppReelsDuration(item.id, it)
                        },
                        canEditReelsDuration = viewModel.canEditBlockDuration(),
                        canEditProtectionSettings = viewModel.canEditBlockDuration(),
                        onFreezeToggle = { enabled ->
                            if (enabled || viewModel.canEditBlockDuration()) {
                                if (enabled && !viewModel.isAccessibilityServiceEnabled()) {
                                    showAccessibilityDialog = true
                                } else {
                                    viewModel.toggleAppFreeze(item.id, enabled)
                                }
                            }
                        },
                        onFreezeDurationSelect = {
                            if (viewModel.canEditBlockDuration()) viewModel.setAppFreezeDuration(item.id, it)
                        },
                        onPeriodMinutesChange = { periodId, minutes ->
                            if (viewModel.canEditBlockDuration()) {
                                viewModel.updateReelsPeriodMinutes(item.id, periodId, minutes)
                            }
                        },
                        onAddPeriod = {
                            if (viewModel.canEditBlockDuration()) {
                                periodStartTime = "08:00"
                                periodEndTime = "12:00"
                                periodDialogAppId = item.id
                            }
                        },
                        onDeletePeriod = { periodId ->
                            if (viewModel.canEditBlockDuration()) {
                                viewModel.deleteReelsPeriod(item.id, periodId)
                            }
                        },
                        freezeDropdownExpanded = expandedFreezeAppId == item.id,
                        onFreezeDropdownChange = { expandedFreezeAppId = if (it) item.id else null },
                        onPeriodsToggle = {
                            if (!item.arePeriodsVisible || viewModel.canEditBlockDuration()) {
                                viewModel.togglePeriodsVisibility(item.id)
                            }
                        }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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

    if (showUsageStatsDialog) {
        AlertDialog(
            onDismissRequest = { showUsageStatsDialog = false },
            title = { Text("السماح بإحصائيات الاستخدام") },
            text = {
                Text("يستخدم MindGuard مدة بقاء التطبيق في الواجهة للتأكد من استمرار مشاهدة الريلز وإظهار الحظر في الوقت الصحيح.")
            },
            confirmButton = {
                Button(onClick = {
                    showUsageStatsDialog = false
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }) { Text("فتح الإعدادات") }
            },
            dismissButton = {
                TextButton(onClick = { showUsageStatsDialog = false }) { Text("لاحقًا") }
            }
        )
    }

    if (periodDialogAppId != null) {
        AlertDialog(
            onDismissRequest = { periodDialogAppId = null },
            title = { Text("تحديد الفترة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { choosePeriodTime(true) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("البداية: $periodStartTime", modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                        Icon(Icons.Outlined.Schedule, contentDescription = null)
                    }
                    OutlinedButton(
                        onClick = { choosePeriodTime(false) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("النهاية: $periodEndTime", modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                        Icon(Icons.Outlined.Schedule, contentDescription = null)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    periodDialogAppId?.let { appId ->
                        viewModel.addReelsPeriod(appId, periodStartTime, periodEndTime)
                    }
                    periodDialogAppId = null
                }) { Text("حفظ الفترة") }
            },
            dismissButton = {
                TextButton(onClick = { periodDialogAppId = null }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun ExpandableAppProtectionCard(
    item: AppProtectionItemState,
    onToggleExpand: () -> Unit,
    isSelected: Boolean,
    onCardBoundsChanged: (Rect) -> Unit,
    onLongPress: () -> Unit,
    onReelsToggle: (Boolean) -> Unit,
    onReelsDurationSelect: (String) -> Unit,
    onReelsMinutesChange: (String) -> Unit,
    canEditReelsDuration: Boolean,
    canEditProtectionSettings: Boolean,
    onFreezeToggle: (Boolean) -> Unit,
    onFreezeDurationSelect: (String) -> Unit,
    onPeriodMinutesChange: (String, String) -> Unit,
    onAddPeriod: () -> Unit,
    onDeletePeriod: (String) -> Unit,
    freezeDropdownExpanded: Boolean,
    onFreezeDropdownChange: (Boolean) -> Unit,
    onPeriodsToggle: () -> Unit
) {
    val durationOptions = listOf("5 دقائق", "10 دقائق", "15 دقيقة", "30 دقيقة", "45 دقيقة", "ساعة", "ساعتان")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                onCardBoundsChanged(coordinates.boundsInWindow())
            }
            .border(
                1.dp,
                if (isSelected) Primary else Color(0xFFE2E8F0),
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F1FF) else Color.White
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(onClick = onToggleExpand, onLongClick = onLongPress)
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isSelected) Primary.copy(alpha = 0.16f) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AppBrandIconBadge(appId = item.id, appName = item.name, icon = item.icon)
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "تم التحديد",
                            tint = Primary,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(17.dp)
                                .background(Color.White, CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFE7EAF0))
                    ProtectionSwitchRow(
                        label = "حظر الريلز",
                        checked = item.isReelsBlocked,
                        enabled = canEditProtectionSettings || !item.isReelsBlocked,
                        onCheckedChange = onReelsToggle
                    )
                    if (item.isReelsBlocked) {
                        OutlinedTextField(
                            value = item.reelsBlockDuration,
                            onValueChange = onReelsMinutesChange,
                            enabled = canEditReelsDuration,
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            ),
                            label = { Text("مدة Reels بالدقائق") },
                            placeholder = { Text("مثال: 30") },
                            colors = protectionNumberFieldColors(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        if (!canEditReelsDuration) {
                            Text(
                                text = "تعديل المدة مقفل لمدة يومين حسب إعدادات الحماية المتقدمة",
                                color = TextGray,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE7EAF0))
                    ProtectionSwitchRow(
                        label = "الفترات",
                        checked = item.arePeriodsVisible,
                        enabled = canEditProtectionSettings || !item.arePeriodsVisible,
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
                                modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(period.name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextDark)
                                        Text("${period.startTime} - ${period.endTime}", fontSize = 9.sp, color = TextGray)
                                    }
                                    OutlinedTextField(
                                        value = period.minutes,
                                        onValueChange = { onPeriodMinutesChange(period.id, it) },
                                        enabled = canEditProtectionSettings,
                                        modifier = Modifier.width(88.dp).height(58.dp),
                                        singleLine = true,
                                        textStyle = LocalTextStyle.current.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 13.sp
                                        ),
                                        label = { Text("دقائق", fontSize = 8.sp) },
                                        colors = protectionNumberFieldColors(),
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    IconButton(
                                        onClick = { onDeletePeriod(period.id) },
                                        enabled = canEditProtectionSettings,
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "حذف الفترة",
                                            tint = Color(0xFFCC3D55),
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                }
                            }
                        }
                        TextButton(
                            onClick = onAddPeriod,
                            enabled = canEditProtectionSettings,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة فترة", fontSize = 11.sp)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE7EAF0))
                    ProtectionSwitchRow(
                        label = "تجميد التطبيق",
                        checked = item.isAppFrozen,
                        enabled = canEditProtectionSettings || !item.isAppFrozen,
                        onCheckedChange = onFreezeToggle
                    )
                    if (item.isAppFrozen) {
                        CompactDurationSelector(
                            value = item.appFreezeDuration,
                            expanded = freezeDropdownExpanded,
                            onExpand = { onFreezeDropdownChange(true) },
                            onDismiss = { onFreezeDropdownChange(false) },
                            options = durationOptions,
                            enabled = canEditProtectionSettings,
                            onSelect = {
                                onFreezeDurationSelect(it)
                                onFreezeDropdownChange(false)
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
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color(0xFF0B1652),
            modifier = Modifier.weight(1f)
        )
        Switch(
            modifier = Modifier.scale(0.86f),
            checked = checked,
            enabled = enabled,
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
private fun protectionNumberFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary
)

@Composable
private fun CompactDurationSelector(
    value: String,
    expanded: Boolean,
    enabled: Boolean = true,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        OutlinedButton(
            onClick = onExpand,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, Color(0xFFD5DAE4)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
        ) {
            Text("المدة: $value", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start, color = MaterialTheme.colorScheme.onSurface)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
        shape = RoundedCornerShape(20.dp),
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
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(22.dp)
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(22.dp)
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
    var showDurationLockConfirmation by remember { mutableStateOf(false) }
    val deviceAdminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refreshPreventUninstallState()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 20.dp)
    ) {
        AppTopBar(title = "الحماية المتقدمة", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "إعدادات الحماية المتقدمة",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Primary,
                textAlign = TextAlign.Right
            )

            Text(
                text = "اختر طريقة تعديل المدة",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: مسموح تعديل المدة
            val isOption1Selected = uiState.blockDurationEditMode == BlockDurationEditMode.ALLOWED_NOW
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clickable { viewModel.setBlockDurationEditMode(BlockDurationEditMode.ALLOWED_NOW) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isOption1Selected) Color(0xFFEFF6FF) else Color.White.copy(alpha = 0.82f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isOption1Selected) 1.5.dp else 1.dp,
                    color = if (isOption1Selected) Primary else Color(0xFFE2E8F0)
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
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = Primary,
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
                        colors = RadioButtonDefaults.colors(selectedColor = Primary)
                    )
                }
            }

            // Option 2: تعديل المدة بعد يومين
            val isOption2Selected = uiState.blockDurationEditMode == BlockDurationEditMode.ALLOW_AFTER_TWO_DAYS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clickable { showDurationLockConfirmation = true },
                colors = CardDefaults.cardColors(
                    containerColor = if (isOption2Selected) Color(0xFFEFF6FF) else Color.White.copy(alpha = 0.82f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isOption2Selected) 1.5.dp else 1.dp,
                    color = if (isOption2Selected) Primary else Color(0xFFE2E8F0)
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
                            tint = Primary,
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
                        onClick = { showDurationLockConfirmation = true },
                        colors = RadioButtonDefaults.colors(selectedColor = Primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prevent Uninstall Section (منع حذف التطبيق)
            SubScreenToggleCard(
                title = "منع حذف التطبيق",
                description = "يتطلب تفعيل Device Admin من إعدادات النظام",
                icon = Icons.Outlined.AdminPanelSettings,
                checked = uiState.isPreventUninstallActive,
                containerColor = Color.White.copy(alpha = 0.82f),
                checkedTrackColor = Primary,
                fixedHeight = 88.dp,
                borderColor = Color(0xFFC9D7EC),
                onCheckedChange = { enabled ->
                    if (enabled) {
                        if (viewModel.isDeviceAdminActive()) {
                            viewModel.togglePreventUninstall(true)
                        } else {
                            deviceAdminLauncher.launch(viewModel.deviceAdminActivationIntent())
                        }
                    } else {
                        viewModel.togglePreventUninstall(false)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showDurationLockConfirmation) {
            AlertDialog(
                onDismissRequest = { showDurationLockConfirmation = false },
                title = { Text("تفعيل منع تعديل المدة") },
                text = {
                    Text(
                        "عند تفعيل هذه الخاصية، لن تتمكن من تغيير حدود الاستخدام المحددة، وذلك لضمان الالتزام بمدة الاستخدام التي تم تعيينها.",
                        textAlign = TextAlign.Right
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showDurationLockConfirmation = false }) {
                        Text("إلغاء", color = Primary)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDurationLockConfirmation = false
                            viewModel.setBlockDurationEditMode(BlockDurationEditMode.ALLOW_AFTER_TWO_DAYS)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("تفعيل")
                    }
                }
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
    AppTopBar(title = title, onBack = onBack)
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
    onCheckedChange: (Boolean) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    checkedTrackColor: Color = Primary,
    fixedHeight: androidx.compose.ui.unit.Dp? = null,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (fixedHeight != null) Modifier.height(fixedHeight)
                else Modifier.heightIn(min = 88.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
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
                shape = RoundedCornerShape(22.dp),
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
                modifier = Modifier.scale(0.86f),
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = checkedTrackColor
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
            shape = RoundedCornerShape(22.dp),
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
