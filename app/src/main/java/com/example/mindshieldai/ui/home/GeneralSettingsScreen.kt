package com.example.mindshieldai.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.GppGood
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindshieldai.ui.theme.DarkBlue
import com.example.mindshieldai.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var usageAlertsEnabled by remember { mutableStateOf(true) }
        var aiInsightsEnabled by remember { mutableStateOf(true) }
        var strictModeEnabled by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "الإعدادات العامة",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else DarkBlue
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = if (isDarkMode) Color.White else DarkBlue)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "إعدادات التطبيق",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else DarkBlue,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        // Language
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Language, contentDescription = null, tint = if (isDarkMode) Color.White else DarkBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("اللغة", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("العربية", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))

                        // Dark Mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.NightsStay, contentDescription = null, tint = if (isDarkMode) Color.White else DarkBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "الوضع الداكن",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = onDarkModeChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Primary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Wellness & AI
                Text(
                    text = "العافية الرقمية والذكاء الاصطناعي",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else DarkBlue,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        // Usage Alerts
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = if (isDarkMode) Color.White else DarkBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("تنبيهات الاستخدام", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("تلقي تنبيهات عند الاقتراب من الحد اليومي", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Switch(
                                checked = usageAlertsEnabled,
                                onCheckedChange = { usageAlertsEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))

                        // AI Insights
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = if (isDarkMode) Color.White else DarkBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("نصائح الذكاء الاصطناعي", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("تحليل سلوك الاستخدام لتقديم نصائح مخصصة", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Switch(
                                checked = aiInsightsEnabled,
                                onCheckedChange = { aiInsightsEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Security
                Text(
                    text = "الأمان",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else DarkBlue,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        // Strict Mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.GppGood, contentDescription = null, tint = if (isDarkMode) Color.White else DarkBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("الوضع الصارم", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("منع تغيير الإعدادات أثناء وقت الحماية", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Switch(
                                checked = strictModeEnabled,
                                onCheckedChange = { strictModeEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDarkMode) Color.DarkGray else Color(0xFFF1F5F9))

                        // App Lock
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = if (isDarkMode) Color.White else DarkBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("قفل التطبيق", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("حماية إعدادات MindShield AI برمز PIN", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Data Management
                Text(
                    text = "إدارة البيانات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else DarkBlue,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "مسح سجل النشاط",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.Red,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "حول التطبيق",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else DarkBlue,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = if (isDarkMode) Color.White else DarkBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("MindShield AI", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Text("الإصدار 1.0.0", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "MindShield AI هو تطبيق متطور يستخدم الذكاء الاصطناعي لحماية توازنك الرقمي وضمان تجربة استخدام آمنة وصحية لك ولعائلتك.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
