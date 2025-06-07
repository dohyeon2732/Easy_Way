package com.example.mobile_project_3.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_project_3.ui_screen.HomeScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // 나중에 추가
        // composable(Screen.Favorites.route) {
        //     Favorites(navController, facilityViewModel)
        // }
        // composable(Screen.MyPage.route) {
        //     MyPage(navController)
        // }
    }
}