package com.example.mobile_project_3.Navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("register")
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object MyPage : Screen("mypage")
}
