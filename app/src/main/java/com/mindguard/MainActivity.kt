package com.mindguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindGuardApp(
                blockedAppName = if (intent.getBooleanExtra(EXTRA_SHOW_BLOCKED, false)) {
                    intent.getStringExtra(EXTRA_BLOCKED_APP).orEmpty()
                } else null
            )
        }
    }

    companion object {
        const val EXTRA_SHOW_BLOCKED = "show_blocked_screen"
        const val EXTRA_BLOCKED_APP = "blocked_app_name"
    }
}
