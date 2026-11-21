package com.mindguard.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguard.core.components.AppTopBar
import com.mindguard.core.theme.DarkBlue
import com.mindguard.core.theme.Primary

enum class GeneralSettingsSubScreen {
    Main,
    Language,
    Theme,
    PrivacyData,
    AppLockPin,
    HelpSupport,
    SupportUs,
    AboutUs
}

@Composable
fun GeneralSettingsScreen(
    onBack: () -> Unit,
    darkThemeEnabled: Boolean = false,
    onDarkThemeChange: (Boolean) -> Unit = {}
) {
    var currentSubScreen by remember { mutableStateOf(GeneralSettingsSubScreen.Main) }
    var aiTipsEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var dailyRemindersEnabled by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        when (currentSubScreen) {
            GeneralSettingsSubScreen.Main -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    AppTopBar(title = "الإعدادات العامة", onBack = onBack)

                    // Section 1: التفضيلات
                    GeneralSettingsSectionTitle("التفضيلات")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column {
                            GeneralSettingsNavRow(
                                title = "اللغة",
                                icon = Icons.Outlined.Language,
                                onClick = { currentSubScreen = GeneralSettingsSubScreen.Language }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = Color(0xFFF1F5F9))
                            GeneralSettingsSwitchRow(
                                title = "نصائح الذكاء الاصطناعي",
                                icon = Icons.Outlined.AutoAwesome,
                                checked = aiTipsEnabled,
                                onCheckedChange = { aiTipsEnabled = it }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = Color(0xFFF1F5F9))
                            GeneralSettingsNavRow(
                                title = "الثيم",
                                icon = Icons.Outlined.Palette,
                                onClick = { currentSubScreen = GeneralSettingsSubScreen.Theme }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 2: التنبيهات
                    GeneralSettingsSectionTitle("التنبيهات")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column {
                            GeneralSettingsSwitchRow(
                                title = "إشعارات التطبيق",
                                icon = Icons.Outlined.Notifications,
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = Color(0xFFF1F5F9))
                            GeneralSettingsSwitchRow(
                                title = "التذكيرات اليومية",
                                icon = Icons.Outlined.CalendarToday,
                                checked = dailyRemindersEnabled,
                                onCheckedChange = { dailyRemindersEnabled = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 3: الخصوصية والأمان
                    GeneralSettingsSectionTitle("الخصوصية والأمان")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column {
                            GeneralSettingsNavRow(
                                title = "الخصوصية والبيانات",
                                icon = Icons.Outlined.Shield,
                                onClick = { currentSubScreen = GeneralSettingsSubScreen.PrivacyData }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = Color(0xFFF1F5F9))
                            GeneralSettingsNavRow(
                                title = "قفل التطبيق ورقم السري",
                                icon = Icons.Outlined.Lock,
                                onClick = { currentSubScreen = GeneralSettingsSubScreen.AppLockPin }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 4: المساعدة
                    GeneralSettingsSectionTitle("المساعدة")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column {
                            GeneralSettingsNavRow(
                                title = "المساعدة والدعم",
                                icon = Icons.AutoMirrored.Outlined.Help,
                                onClick = { currentSubScreen = GeneralSettingsSubScreen.HelpSupport }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = Color(0xFFF1F5F9))
                            GeneralSettingsNavRow(
                                title = "دعمنا",
                                icon = Icons.Outlined.FavoriteBorder,
                                onClick = { currentSubScreen = GeneralSettingsSubScreen.SupportUs }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = Color(0xFFF1F5F9))
                            GeneralSettingsNavRow(
                                title = "من نحن",
                                icon = Icons.Outlined.PeopleOutline,
                                onClick = { currentSubScreen = GeneralSettingsSubScreen.AboutUs }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Footer Version
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "الإصدار 1.0.0",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            GeneralSettingsSubScreen.Language -> LanguageSettingsScreen(onBack = { currentSubScreen = GeneralSettingsSubScreen.Main })
            GeneralSettingsSubScreen.Theme -> ThemeSettingsScreen(
                onBack = { currentSubScreen = GeneralSettingsSubScreen.Main },
                darkThemeEnabled = darkThemeEnabled,
                onDarkThemeChange = onDarkThemeChange
            )
            GeneralSettingsSubScreen.PrivacyData -> PrivacyDataScreen(onBack = { currentSubScreen = GeneralSettingsSubScreen.Main })
            GeneralSettingsSubScreen.AppLockPin -> AppLockPinScreen(onBack = { currentSubScreen = GeneralSettingsSubScreen.Main })
            GeneralSettingsSubScreen.HelpSupport -> HelpSupportScreen(onBack = { currentSubScreen = GeneralSettingsSubScreen.Main })
            GeneralSettingsSubScreen.SupportUs -> SupportUsScreen(onBack = { currentSubScreen = GeneralSettingsSubScreen.Main })
            GeneralSettingsSubScreen.AboutUs -> AboutUsScreen(onBack = { currentSubScreen = GeneralSettingsSubScreen.Main })
        }
    }
}

@Composable
fun GeneralSettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = DarkBlue,
        fontSize = 15.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun GeneralSettingsNavRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DarkBlue,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun GeneralSettingsSwitchRow(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DarkBlue,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary
            ),
            modifier = Modifier.scale(0.75f)
        )
    }
}
