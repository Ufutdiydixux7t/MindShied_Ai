package com.mindguard.navigation

sealed class AppRoute(val route: String) {
    object Login : AppRoute("login")
    object Register : AppRoute("register")
    object Home : AppRoute("home/{userName}") {
        fun createRoute(userName: String) = "home/$userName"
    }
    object ChildActivation : AppRoute("child_activation")
    object AccountProfile : AppRoute("account_profile")
    object GeneralSettings : AppRoute("general_settings")
}
