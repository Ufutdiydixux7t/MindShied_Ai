package com.mindguard

import android.net.Uri
import android.content.Intent
import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.mindguard.core.theme.MindShieldAiTheme
import com.mindguard.feature.protection.BlockedScreen
import com.mindguard.navigation.AppNavHost

@Composable
fun MindGuardApp(
    blockedAppName: String? = null,
    blockedPackageName: String? = null
) {
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var showBlockedScreen by remember(blockedAppName) { mutableStateOf(blockedAppName != null) }
    val context = LocalContext.current

    MindShieldAiTheme {
        if (showBlockedScreen && blockedAppName != null) {
            BlockedScreen(
                blockedAppName = blockedAppName,
                onReturnHome = {
                    val launchIntent = blockedPackageName?.let { context.packageManager.getLaunchIntentForPackage(it) }
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context.startActivity(launchIntent)
                        (context as? Activity)?.finish()
                    } else {
                        showBlockedScreen = false
                    }
                }
            )
        } else {
            val navController = rememberNavController()
            AppNavHost(
                navController = navController,
                startAtHome = blockedAppName != null,
                profileImageUri = profileImageUri,
                onProfileImageChange = { newUri ->
                    profileImageUri = newUri ?: profileImageUri
                }
            )
        }
    }
}
