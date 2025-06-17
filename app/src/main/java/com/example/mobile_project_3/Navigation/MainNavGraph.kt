package com.example.mobile_project_3.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_project_3.ui_screen.Favorites
import com.example.mobile_project_3.ui_screen.HomeScreen
import com.example.mobile_project_3.ui_screen.MyPage
import com.example.mobile_project_3.viewmodel.FacilityViewModel
<<<<<<< HEAD

@Composable
fun MainNavGraph(navController: NavHostController, facilityViewModel: FacilityViewModel) {
=======
import com.example.mobile_project_3.viewmodel.UserViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    facilityViewModel: FacilityViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    userViewModel: UserViewModel
) {
>>>>>>> 964ebc6 (Initial commit)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
<<<<<<< HEAD
            HomeScreen(navController, facilityViewModel) // ✅ 전달
        }
        composable(Screen.Favorites.route) {
            Favorites(navController, facilityViewModel) // ✅ 전달
        }
        composable(Screen.MyPage.route) {
            MyPage(navController) // 필요 시 전달 가능
=======
            HomeScreen(navController, facilityViewModel, isDarkTheme)
        }
        composable(Screen.Favorites.route) {
            Favorites(
                navController = navController,
                viewModel = facilityViewModel,
                isDarkTheme = isDarkTheme
            )
        }
        composable(Screen.MyPage.route) {
            MyPage(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                userViewModel = userViewModel
            )
>>>>>>> 964ebc6 (Initial commit)
        }
    }
}