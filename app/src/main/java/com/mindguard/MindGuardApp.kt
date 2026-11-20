package com.mindguard

import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.mindguard.core.theme.MindShieldAiTheme
import com.mindguard.navigation.AppNavHost

@Composable
fun MindGuardApp() {
    var isDarkMode by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    MindShieldAiTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()
        AppNavHost(
            navController = navController,
            isDarkMode = isDarkMode,
            onDarkModeChange = { isDarkMode = it },
            profileImageUri = profileImageUri,
            onProfileImageChange = { newUri ->
                profileImageUri = newUri ?: profileImageUri
            }
        )
    }
}
