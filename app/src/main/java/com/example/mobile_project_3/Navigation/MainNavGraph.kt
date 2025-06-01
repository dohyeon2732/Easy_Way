package com.example.mobile_project_3.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_project_3.ui_screen.Favorites
import com.example.mobile_project_3.ui_screen.HomeScreen
import com.example.mobile_project_3.ui_screen.MyPage
import com.example.mobile_project_3.viewmodel.FacilityViewModel

@Composable
fun MainNavGraph(navController: NavHostController, facilityViewModel: FacilityViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController, facilityViewModel) // ✅ 전달
        }
        composable(Screen.Favorites.route) {
            Favorites(navController, facilityViewModel) // ✅ 전달
        }
        composable(Screen.MyPage.route) {
            MyPage(navController) // 필요 시 전달 가능
        }
    }
}