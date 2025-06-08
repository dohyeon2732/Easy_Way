package com.example.mobile_project_3.ui_screen

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobile_project_3.viewmodel.FacilityData
import com.example.mobile_project_3.viewmodel.FacilityViewModel

@Composable
fun Favorites(navController: NavController, viewModel: FacilityViewModel) {
    val favoriteFacilities by viewModel.favoriteFacilities.collectAsState()
    var selectedFacility by remember { mutableStateOf<FacilityData?>(null) }

    Log.d("favoriteList","${favoriteFacilities}")

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                onItemClick = { facility ->
                    selectedFacility = facility
                }
            )
        }

        // 👉 화면 위에 떠야 하므로 Box 안쪽에서 오버레이로 띄우기
        selectedFacility?.let { facility ->
            FacilityDetailOverlayCard(
                facility = facility,
                onDismiss = { selectedFacility = null },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
