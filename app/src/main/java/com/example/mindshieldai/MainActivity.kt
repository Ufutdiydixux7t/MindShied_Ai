package com.example.mindshieldai

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mindshieldai.ui.home.AccountProfileScreen
import com.example.mindshieldai.ui.home.GeneralSettingsScreen
import com.example.mindshieldai.ui.home.HomeScreen
import com.example.mindshieldai.ui.login.LoginScreen
import com.example.mindshieldai.ui.register.RegisterScreen
import com.example.mindshieldai.ui.theme.MindShieldAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            var profileImageUri by remember { mutableStateOf<Uri?>(null) }

            MindShieldAiTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginClick = { name, _ ->
                                val finalName = if (name.isNotBlank()) name else "المستخدم"
                                navController.navigate("home/$finalName")
                            },
                            onRegisterClick = {
                                navController.navigate("register")
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onBackToLogin = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(
                        "home/{userName}",
                        arguments = listOf(navArgument("userName") { defaultValue = "المستخدم" })
                    ) { backStackEntry ->
                        val userName = backStackEntry.arguments?.getString("userName") ?: "المستخدم"
                        HomeScreen(
                            userName = userName,
                            navController = navController,
                            isDarkMode = isDarkMode,
                            onDarkModeChange = { isDarkMode = it },
                            profileImageUri = profileImageUri
                        )
                    }
                    composable("account_profile") {
                        AccountProfileScreen(
                            profileImageUri = profileImageUri,
                            onProfileImageChange = { newUri -> profileImageUri = if (newUri != null) newUri else profileImageUri },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("general_settings") {
                        GeneralSettingsScreen(
                            isDarkMode = isDarkMode,
                            onDarkModeChange = { isDarkMode = it },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
