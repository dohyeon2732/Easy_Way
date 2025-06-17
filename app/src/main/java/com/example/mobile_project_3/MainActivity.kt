package com.example.mobile_project_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
<<<<<<< HEAD
=======
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
>>>>>>> 964ebc6 (Initial commit)
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mobile_project_3.Navigation.EasyWayNavGraph
import com.example.mobile_project_3.ui.theme.Mobile_project_3Theme
import com.example.mobile_project_3.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
<<<<<<< HEAD
            Mobile_project_3Theme {
                AppNavigator()
=======
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            Mobile_project_3Theme(darkTheme = isDarkTheme) {
                AppNavigator(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it }
                )
>>>>>>> 964ebc6 (Initial commit)
            }
        }
    }
}

@Composable
<<<<<<< HEAD
fun AppNavigator() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    EasyWayNavGraph(navController = navController, userViewModel = userViewModel)
=======
fun AppNavigator(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    EasyWayNavGraph(
        navController = navController,
        userViewModel = userViewModel,
        isDarkTheme = isDarkTheme,
        onThemeChange = onThemeChange
    )
>>>>>>> 964ebc6 (Initial commit)
}
