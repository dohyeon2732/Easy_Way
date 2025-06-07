package com.example.mobile_project_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile_project_3.ui.theme.Mobile_project_3Theme
import com.example.mobile_project_3.Navigation.EasyWayNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_project_3Theme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    EasyWayNavGraph(navController = navController)
}
