package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobile_project_3.viewmodel.FacilityViewModel

@Composable
fun Favorites(navController: NavController, viewModel: FacilityViewModel) {
    val facilities by viewModel.facilities.collectAsState() // ✅ 구독
    val favoriteFacilities = facilities.filter { it.isFavorite } // ✅ 리스트로 필터링

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "즐겨찾기",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "시설 목록",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FacilityList(
            facilities = favoriteFacilities,
            showTopOnly = true,
            onUserScroll = {},
            onCollapseRequest = {},
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onItemClick = {}
        )
    }
}
