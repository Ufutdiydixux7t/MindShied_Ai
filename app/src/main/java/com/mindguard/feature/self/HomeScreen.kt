package com.mindguard.feature.self

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mindguard.core.components.AppTopBar
import com.mindguard.core.components.BottomNavigationBar
import com.mindguard.feature.parent.ParentHomeScreen
import com.mindguard.feature.protection.ProtectionScreen
import com.mindguard.feature.settings.SettingsScreen
import com.mindguard.feature.usage.UsageScreen

@Composable
fun HomeScreen(
    userName: String = "أحمد",
    navController: NavController? = null,
    profileImageUri: Uri? = null,
    darkThemeEnabled: Boolean = false,
    onDarkThemeChange: (Boolean) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showBottomBar by remember { mutableStateOf(true) }
    val isHome = selectedTab == 0

    LaunchedEffect(selectedTab) {
        if (selectedTab != 2 && selectedTab != 4) {
            showBottomBar = true
        }
    }

    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.statusBars
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                ) {
                    if (selectedTab != 2 && selectedTab != 4) {
                        AppTopBar(
                            title = when (selectedTab) {
                                0 -> "الرئيسية"
                                1 -> "الإحصائيات"
                                3 -> "العائلة"
                                else -> ""
                            },
                            userName = if (isHome) userName else null,
                            profileImageUri = if (isHome) profileImageUri else null
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        color = MaterialTheme.colorScheme.surface,
                        shape = if (isHome) RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp) else RoundedCornerShape(0.dp)
                    ) {
                        when (selectedTab) {
                            0 -> SelfHomeScreen()
                            1 -> UsageScreen()
                            2 -> ProtectionScreen(
                                onSubScreenVisibilityChange = { showBottomBar = it }
                            )
                            3 -> ParentHomeScreen(
                                onSubScreenVisibilityChange = { showBottomBar = it }
                            )
                            4 -> SettingsScreen(
                                onLogout = {
                                    navController?.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToTab = { selectedTab = it },
                                navController = navController,
                                profileImageUri = profileImageUri,
                                darkThemeEnabled = darkThemeEnabled,
                                onDarkThemeChange = onDarkThemeChange,
                                onSubScreenVisibilityChange = { showBottomBar = it }
                            )
                        }
                    }
                }

                if (showBottomBar) {
                    BottomNavigationBar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        liftedForShortContent = false,
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }
        }
    }
}
