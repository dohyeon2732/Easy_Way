package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mobile_project_3.Navigation.MainNavGraph
import com.example.mobile_project_3.viewmodel.FacilityViewModel

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val facilityViewModel: FacilityViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavGraph(navController,facilityViewModel)
        }
    }
}