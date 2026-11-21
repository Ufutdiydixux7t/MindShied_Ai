package com.mindguard.feature.settings

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mindguard.core.components.AppTopBar
import com.mindguard.core.theme.DarkBlue

enum class MoreSubScreen {
    Main,
    GeneralSettings,
    AppsPlaceholder,
    NotificationsPlaceholder,
    ShareAppPlaceholder,
    PrivacyPlaceholder,
    SecurityPinPlaceholder
}

@Composable
fun SettingsScreen(
    onLogout: () -> Unit = {},
    onNavigateToTab: (Int) -> Unit = {},
    navController: NavController? = null,
    profileImageUri: Uri? = null,
    darkThemeEnabled: Boolean = false,
    onDarkThemeChange: (Boolean) -> Unit = {},
    onSubScreenVisibilityChange: (Boolean) -> Unit = {}
) {
    var currentSubScreen by remember { mutableStateOf(MoreSubScreen.Main) }

    LaunchedEffect(currentSubScreen) {
        onSubScreenVisibilityChange(currentSubScreen == MoreSubScreen.Main)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        when (currentSubScreen) {
            MoreSubScreen.Main -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppTopBar(title = "الإعدادات")
                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Profile Header Card (Matching More screen screenshot)
                    ProfileHeaderCard(
                        name = "أحمد محمد",
                        email = "ahmed.mohamed@example.com",
                        profileImageUri = profileImageUri,
                        onClick = { navController?.navigate("account_profile") }
                    )

                    // 2. Section Title: الإعدادات
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // 3. Settings List Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column {
                            MoreSettingsRow(
                                title = "الإعدادات العامة",
                                icon = Icons.Outlined.Settings,
                                onClick = { currentSubScreen = MoreSubScreen.GeneralSettings }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                            MoreSettingsRow(
                                title = "الحماية",
                                icon = Icons.Outlined.Shield,
                                onClick = { onNavigateToTab(2) }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                            MoreSettingsRow(
                                title = "التطبيقات",
                                icon = Icons.Outlined.GridView,
                                onClick = { currentSubScreen = MoreSubScreen.AppsPlaceholder }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                            MoreSettingsRow(
                                title = "الإشعارات",
                                icon = Icons.Outlined.Notifications,
                                onClick = { currentSubScreen = MoreSubScreen.NotificationsPlaceholder }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                            MoreSettingsRow(
                                title = "مشاركة التطبيق",
                                icon = Icons.Outlined.Share,
                                onClick = { currentSubScreen = MoreSubScreen.ShareAppPlaceholder }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                            MoreSettingsRow(
                                title = "الخصوصية",
                                icon = Icons.Outlined.Lock,
                                onClick = { currentSubScreen = MoreSubScreen.PrivacyPlaceholder }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                            MoreSettingsRow(
                                title = "الأمان و PIN",
                                icon = Icons.Outlined.GppGood,
                                onClick = { currentSubScreen = MoreSubScreen.SecurityPinPlaceholder }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                            MoreSettingsRow(
                                title = "العائلة",
                                icon = Icons.Outlined.FamilyRestroom,
                                onClick = { onNavigateToTab(3) }
                            )
                        }
                    }

                    // 4. Logout Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        onClick = onLogout,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(20.dp)
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
                                color = Color.Red,
                                modifier = Modifier.weight(1f)
                            )
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

            MoreSubScreen.GeneralSettings -> GeneralSettingsScreen(
                onBack = { currentSubScreen = MoreSubScreen.Main },
                darkThemeEnabled = darkThemeEnabled,
                onDarkThemeChange = onDarkThemeChange
            )

            MoreSubScreen.AppsPlaceholder -> PlaceholderMoreScreen(title = "التطبيقات", onBack = { currentSubScreen = MoreSubScreen.Main })
            MoreSubScreen.NotificationsPlaceholder -> PlaceholderMoreScreen(title = "الإشعارات", onBack = { currentSubScreen = MoreSubScreen.Main })
            MoreSubScreen.ShareAppPlaceholder -> PlaceholderMoreScreen(title = "مشاركة التطبيق", onBack = { currentSubScreen = MoreSubScreen.Main })
            MoreSubScreen.PrivacyPlaceholder -> PlaceholderMoreScreen(title = "الخصوصية", onBack = { currentSubScreen = MoreSubScreen.Main })
            MoreSubScreen.SecurityPinPlaceholder -> PlaceholderMoreScreen(title = "الأمان و PIN", onBack = { currentSubScreen = MoreSubScreen.Main })
        }
    }
}

@Composable
fun ProfileHeaderCard(
    name: String,
    email: String,
    profileImageUri: Uri? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Chevron Arrow < (in RTL)
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = DarkBlue,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Name and Email (Right-aligned in RTL)
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Profile Avatar with Camera badge
            Box(contentAlignment = Alignment.BottomStart) {
                Surface(
                    modifier = Modifier.size(54.dp),
                    shape = CircleShape,
                        color = Color(0xFFE2E8F0)
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
                            tint = Color.Gray,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(18.dp)
                        .offset(x = 2.dp, y = (-2).dp),
                    shape = CircleShape,
                    color = DarkBlue,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.padding(3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MoreSettingsRow(
    title: String,
    icon: ImageVector,
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
            tint = DarkBlue,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun PlaceholderMoreScreen(title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(title = title, onBack = onBack)

        Box(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("واجهة $title فارغة", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
