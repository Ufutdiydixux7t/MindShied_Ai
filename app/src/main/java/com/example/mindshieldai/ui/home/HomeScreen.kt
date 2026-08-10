package com.example.mindshieldai.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindshieldai.ui.theme.DarkBlue
import com.example.mindshieldai.ui.theme.Primary

enum class SettingsSubScreen {
    Main, Account, Notifications, Privacy, Security
}

@Composable
fun HomeScreen(userName: String = "أحمد", navController: NavController? = null) {
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
            containerColor = if (isHome) DarkBlue else Color.White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isHome) {
                    HomeTopBar(currentUserName)
                }

                // Content Area
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    shape = if (isHome) RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp) else RoundedCornerShape(0.dp)
                ) {
                    when (selectedTab) {
                        0 -> MainContent()
                        1 -> StatsContent()
                        2 -> ProtectionContent()
                        3 -> MonitoringContent()
                        4 -> MoreContent(
                            userName = currentUserName,
                            onNameChange = { currentUserName = it },
                            onLogout = {
                                navController?.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToTab = { selectedTab = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("الواجهة الرئيسية", style = MaterialTheme.typography.titleLarge)
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
    userName: String,
    onNameChange: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToTab: (Int) -> Unit
) {
    var subScreen by remember { mutableStateOf(SettingsSubScreen.Main) }
    var showNameDialog by remember { mutableStateOf(false) }

    when (subScreen) {
        SettingsSubScreen.Main -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Could go back to home tab */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Back")
                    }
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    SettingsItem(
                        title = "الحساب",
                        description = "إدارة معلومات حسابك واشتراكاتك.",
                        icon = Icons.Default.Person,
                        iconContainerColor = Color(0xFFE3F2FD),
                        iconColor = Color(0xFF1976D2),
                        onClick = { subScreen = SettingsSubScreen.Account }
                    )

                    SettingsItem(
                        title = "الحماية",
                        description = "إدارة إعدادات الحماية والمستوى الذكي.",
                        icon = Icons.Default.Shield,
                        iconContainerColor = Color(0xFFE8F5E9),
                        iconColor = Color(0xFF43A047),
                        onClick = { onNavigateToTab(2) }
                    )

                    SettingsItem(
                        title = "الإشعارات",
                        description = "تخصيص الإشعارات والتنبيهات.",
                        icon = Icons.Default.Notifications,
                        iconContainerColor = Color(0xFFF3E5F5),
                        iconColor = Color(0xFF9C27B0),
                        onClick = { subScreen = SettingsSubScreen.Notifications }
                    )

                    SettingsItem(
                        title = "الخصوصية",
                        description = "إدارة بياناتك وصلاحيات التطبيق.",
                        icon = Icons.Default.Lock,
                        iconContainerColor = Color(0xFFE3F2FD),
                        iconColor = Color(0xFF1976D2),
                        onClick = { subScreen = SettingsSubScreen.Privacy }
                    )

                    SettingsItem(
                        title = "الأمان",
                        description = "خيارات الأمان والتحقق الثنائي.",
                        icon = Icons.Default.Fingerprint,
                        iconContainerColor = Color(0xFFFFF3E0),
                        iconColor = Color(0xFFFB8C00),
                        onClick = { subScreen = SettingsSubScreen.Security }
                    )

                    SettingsItem(
                        title = "الرقابة الأبوية",
                        description = "إدارة إعدادات الرقابة والأوقات المسموح بها.",
                        icon = Icons.Default.Group,
                        iconContainerColor = Color(0xFFEDE7F6),
                        iconColor = Color(0xFF5E35B1),
                        onClick = { onNavigateToTab(3) }
                    )

                    SettingsItem(
                        title = "الإحصائيات",
                        description = "عرض تقارير الاستخدام والنشاط.",
                        icon = Icons.Default.BarChart,
                        iconContainerColor = Color(0xFFFFEBEE),
                        iconColor = Color(0xFFD32F2F),
                        onClick = { onNavigateToTab(1) }
                    )
                }
            }
        }
        SettingsSubScreen.Account -> {
            AccountSettingsScreen(
                userName = userName,
                onBack = { subScreen = SettingsSubScreen.Main },
                onNameChange = { showNameDialog = true },
                onLogout = onLogout
            )
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

    // Name Change Dialog
    if (showNameDialog) {
        var newName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("تغيير الاسم") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("الاسم الجديد") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onNameChange(newName)
                    showNameDialog = false
                }) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun AccountSettingsScreen(
    userName: String,
    onBack: () -> Unit,
    onNameChange: () -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SettingsHeader(title = "الحساب", onBack = onBack)
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("الاسم الحالي: $userName", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("تغيير الاسم")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Text("تسجيل الخروج")
                }
            }
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconContainerColor
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun HomeTopBar(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Right side: Greeting
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
        containerColor = Color.White,
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
