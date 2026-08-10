package com.example.mindshieldai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mindshieldai.ui.home.HomeScreen
import com.example.mindshieldai.ui.login.LoginScreen
import com.example.mindshieldai.ui.theme.MindShieldAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindShieldAiTheme(darkTheme = false) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginClick = { name, _ ->
                                val finalName = if (name.isNotBlank()) name else "المستخدم"
                                navController.navigate("home/$finalName")
                            }
                        )
                    }
                    composable(
                        "home/{userName}",
                        arguments = listOf(navArgument("userName") { defaultValue = "المستخدم" })
                    ) { backStackEntry ->
                        val userName = backStackEntry.arguments?.getString("userName") ?: "المستخدم"
                        HomeScreen(userName = userName, navController = navController)
                    }
                }
            }
        }
    }
}
