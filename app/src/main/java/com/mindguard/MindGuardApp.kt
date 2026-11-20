package com.mindguard

import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.mindguard.core.theme.MindShieldAiTheme
import com.mindguard.feature.protection.BlockedScreen
import com.mindguard.navigation.AppNavHost

@Composable
fun MindGuardApp(blockedAppName: String? = null) {
    var isDarkMode by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var showBlockedScreen by remember(blockedAppName) { mutableStateOf(blockedAppName != null) }

    MindShieldAiTheme(darkTheme = isDarkMode) {
        if (showBlockedScreen && blockedAppName != null) {
            BlockedScreen(
                blockedAppName = blockedAppName,
                onReturnHome = { showBlockedScreen = false }
            )
        } else {
            val navController = rememberNavController()
            AppNavHost(
                navController = navController,
                startAtHome = blockedAppName != null,
                isDarkMode = isDarkMode,
                onDarkModeChange = { isDarkMode = it },
                profileImageUri = profileImageUri,
                onProfileImageChange = { newUri ->
                    profileImageUri = newUri ?: profileImageUri
                }
            )
        }
    }
}
