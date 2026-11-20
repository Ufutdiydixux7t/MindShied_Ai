package com.mindguard.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mindguard.feature.auth.LoginScreen
import com.mindguard.feature.auth.RegisterScreen
import com.mindguard.feature.child.ChildActivationScreen
import com.mindguard.feature.self.HomeScreen
import com.mindguard.feature.settings.AccountProfileScreen
import com.mindguard.feature.settings.GeneralSettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startAtHome: Boolean = false,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    profileImageUri: Uri?,
    onProfileImageChange: (Uri?) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (startAtHome) AppRoute.Home.createRoute("المستخدم") else AppRoute.Login.route
    ) {
        composable(AppRoute.Login.route) {
            LoginScreen(
                onLoginClick = { name, _ ->
                    val finalName = if (name.isNotBlank()) name else "المستخدم"
                    navController.navigate(AppRoute.Home.createRoute(finalName))
                },
                onRegisterClick = {
                    navController.navigate(AppRoute.Register.route)
                },
                onChildJoinClick = {
                    navController.navigate(AppRoute.ChildActivation.route)
                }
            )
        }

        composable(AppRoute.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.ChildActivation.route) {
            ChildActivationScreen(
                onBack = {
                    navController.popBackStack()
                },
                onActivationSuccess = {
                    navController.navigate(AppRoute.Home.createRoute("الطفل")) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            AppRoute.Home.route,
            arguments = listOf(navArgument("userName") { defaultValue = "المستخدم" })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "المستخدم"
            HomeScreen(
                userName = userName,
                navController = navController,
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                profileImageUri = profileImageUri
            )
        }

        composable(AppRoute.AccountProfile.route) {
            AccountProfileScreen(
                profileImageUri = profileImageUri,
                onProfileImageChange = onProfileImageChange,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.GeneralSettings.route) {
            GeneralSettingsScreen(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
