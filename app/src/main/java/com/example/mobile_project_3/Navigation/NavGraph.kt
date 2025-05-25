package com.example.mobile_project_3.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_project_3.ui_screen.Favorites
import com.example.mobile_project_3.ui_screen.HomeScreen
import com.example.mobile_project_3.ui_screen.LoginScreen
import com.example.mobile_project_3.ui_screen.MyPage
import com.example.mobile_project_3.ui_screen.SignupScreen
import com.example.mobile_project_3.ui_screen.SplashScreen

@Composable
fun EasyWayNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Signup.route) { SignupScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Favorites.route) { Favorites(navController) }
        composable(Screen.MyPage.route) { MyPage(navController) }
    }
}
