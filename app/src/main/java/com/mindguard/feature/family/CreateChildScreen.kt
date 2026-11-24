package com.mindguard.feature.family

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguard.core.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChildScreen(
    onBack: () -> Unit,
    onNextToLink: (FamilyChildDraft) -> Unit,
    initialDraft: FamilyChildDraft? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var childName by remember(initialDraft) { mutableStateOf(initialDraft?.name.orEmpty()) }
        var childEmail by remember(initialDraft) { mutableStateOf(initialDraft?.email.orEmpty()) }
        var childAge by remember(initialDraft) { mutableStateOf(initialDraft?.age?.toString().orEmpty()) }
        var formError by remember { mutableStateOf<String?>(null) }

        val initialSettings = initialDraft?.protectionSettings ?: FamilyProtectionSettings()
        var isShortVideosLimitEnabled by remember(initialDraft) { mutableStateOf(initialSettings.shortVideosLimitEnabled) }
        var isProtectedAppsEnabled by remember(initialDraft) { mutableStateOf(initialSettings.protectedAppsEnabled) }
        var isSleepTimeEnabled by remember(initialDraft) { mutableStateOf(initialSettings.sleepTimeEnabled) }
        var isContentProtectionEnabled by remember(initialDraft) { mutableStateOf(initialSettings.contentProtectionEnabled) }
        var isLockRulesEnabled by remember(initialDraft) { mutableStateOf(initialSettings.lockRulesEnabled) }
        var isExtraTimeEnabled by remember(initialDraft) { mutableStateOf(initialSettings.extraTimeEnabled) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "إضافة طفل جديد",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "الخطوة ١ من ٢",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        formError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Button(
                            onClick = {
                                val parsedAge = childAge.toIntOrNull()
                                formError = when {
                                    childName.isBlank() -> "أدخل اسم الطفل للمتابعة."
                                    !android.util.Patterns.EMAIL_ADDRESS.matcher(childEmail.trim()).matches() -> "أدخل بريداً إلكترونياً صحيحاً."
                                    parsedAge == null || parsedAge !in 1..18 -> "أدخل عمراً صحيحاً بين ١ و١٨ سنة."
                                    else -> null
                                }
                                if (formError == null) {
                                    onNextToLink(
                                        FamilyChildDraft(
                                            name = childName.trim(),
                                            email = childEmail.trim(),
                                            age = parsedAge!!,
                                            protectionSettings = FamilyProtectionSettings(
                                                shortVideosLimitEnabled = isShortVideosLimitEnabled,
                                                protectedAppsEnabled = isProtectedAppsEnabled,
                                                sleepTimeEnabled = isSleepTimeEnabled,
                                                contentProtectionEnabled = isContentProtectionEnabled,
                                                lockRulesEnabled = isLockRulesEnabled,
                                                extraTimeEnabled = isExtraTimeEnabled
                                            )
                                        )
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "التالي: مراجعة الملف المحلي",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "جميع قواعد الحماية قابلة للتعديل لاحقاً",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Title & Subtitle
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "أنشئ ملف طفلك",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "أدخل بيانات طفلك الأساسية واضبط قواعد الحماية الذكية المناسبة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Section 1: Child Data
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonOutline, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بيانات الطفل",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = childName,
                                onValueChange = { childName = it; formError = null },
                            label = { Text("اسم الطفل", fontSize = 13.sp) },
                            placeholder = { Text("مثال: آدم", fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = Primary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = childEmail,
                            onValueChange = { childEmail = it; formError = null },
                            label = { Text("البريد الإلكتروني", fontSize = 13.sp) },
                            placeholder = { Text("name@example.com", fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = Primary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = childAge,
                            onValueChange = { childAge = it.filter(Char::isDigit); formError = null },
                            label = { Text("العمر", fontSize = 13.sp) },
                            placeholder = { Text("مثال: ١٠ سنوات", fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Cake, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = Primary
                            )
                        )
                    }
                }

                // Section 2: Protection Rules
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "قواعد الحماية",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Text(
                        text = "يضبطها الوالد ويمكن مراجعتها وتعديلها في أي وقت.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "في النسخة المحلية تُحفظ هذه القواعد على جهاز ولي الأمر فقط ولا تُطبّق على جهاز الطفل.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Rule 1: Short Videos Limit
                    RuleToggleCard(
                        title = "حد الفيديوهات القصيرة",
                        subtitle = "حظر أو تقييد الريلز والفيديوهات القصيرة",
                        icon = Icons.Outlined.PlayCircle,
                        checked = isShortVideosLimitEnabled,
                        onCheckedChange = { isShortVideosLimitEnabled = it },
                        badgeText = "تفضيل محلي"
                    )

                    // Rule 2: Protected Apps
                    RuleToggleCardWithChips(
                        title = "تطبيقات محمية",
                        subtitle = "تأمين وحماية التطبيقات المحددة",
                        icon = Icons.Outlined.GridView,
                        checked = isProtectedAppsEnabled,
                        onCheckedChange = { isProtectedAppsEnabled = it },
                        chips = listOf("Instagram", "TikTok", "YouTube")
                    )

                    // Rule 3: Sleep / Rest Time
                    RuleToggleCard(
                        title = "وقت الراحة والنوم",
                        subtitle = "إيقاف استخدام الشاشة خلال ساعات النوم",
                        icon = Icons.Outlined.Bedtime,
                        checked = isSleepTimeEnabled,
                        onCheckedChange = { isSleepTimeEnabled = it },
                        badgeText = "تفضيل محلي"
                    )

                    // Rule 4: Content Protection
                    RuleToggleCard(
                        title = "حماية المحتوى",
                        subtitle = "حجب المحتوى غير المناسب أو البالغين",
                        icon = Icons.Outlined.Shield,
                        checked = isContentProtectionEnabled,
                        onCheckedChange = { isContentProtectionEnabled = it }
                    )

                    // Rule 5: Lock Rule Changes
                    RuleToggleCard(
                        title = "قفل تغييرات القواعد",
                        subtitle = "لا تُعدّل الإعدادات إلا برمز PIN للوالد",
                        icon = Icons.Outlined.Lock,
                        checked = isLockRulesEnabled,
                        onCheckedChange = { isLockRulesEnabled = it }
                    )

                    // Rule 6: Request Extra Time
                    RuleToggleCard(
                        title = "طلب وقت إضافي",
                        subtitle = "تفضيل محلي؛ لا تصل طلبات من جهاز الطفل في هذا الوضع",
                        icon = Icons.Outlined.Update,
                        checked = isExtraTimeEnabled,
                        onCheckedChange = { isExtraTimeEnabled = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RuleToggleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    badgeText: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(9.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE0E7FF)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.scale(0.85f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = Color(0xFFE2E8F0)
                )
            )
        }
    }
}

@Composable
fun RuleToggleCardWithChips(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    chips: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(9.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.scale(0.85f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = Color(0xFFE2E8F0)
                    )
                )
            }

            if (chips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chips.forEach { chip ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE0E7FF)
                        ) {
                            Text(
                                text = chip,
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
