package com.example.mobile_project_3.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_project_3.ui_screen.LoginScreen
import com.example.mobile_project_3.ui_screen.MainScreen
import com.example.mobile_project_3.ui_screen.SignupScreen
import com.example.mobile_project_3.ui_screen.SplashScreen

@Composable
fun EasyWayNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Signup.route) { SignupScreen(navController) }
        composable(Screen.Home.route) {
            MainScreen() // 내부에서 다시 NavController 생성해서 MainNavGraph 사용
        }
    }
}
