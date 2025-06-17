package com.example.mobile_project_3.ui_screen

import android.util.Log
import androidx.compose.foundation.background
<<<<<<< HEAD
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
=======
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
>>>>>>> 964ebc6 (Initial commit)
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
<<<<<<< HEAD
=======
import androidx.compose.ui.text.font.FontWeight
>>>>>>> 964ebc6 (Initial commit)
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobile_project_3.viewmodel.FacilityData
import com.example.mobile_project_3.viewmodel.FacilityViewModel

@Composable
<<<<<<< HEAD
fun Favorites(navController: NavController, viewModel: FacilityViewModel) {
=======
fun Favorites(
    navController: NavController,
    viewModel: FacilityViewModel,
    isDarkTheme: Boolean
) {
>>>>>>> 964ebc6 (Initial commit)
    val favoriteFacilities by viewModel.favoriteFacilities.collectAsState()
    var selectedFacility by remember { mutableStateOf<FacilityData?>(null) }

    Log.d("favoriteList","${favoriteFacilities}")

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
<<<<<<< HEAD
            .background(Color.White)
=======
            .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White)
>>>>>>> 964ebc6 (Initial commit)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
<<<<<<< HEAD
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "즐겨찾기",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )
=======
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "즐겨찾기",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }

            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))
>>>>>>> 964ebc6 (Initial commit)

            Text(
                text = "시설 목록",
                fontSize = 14.sp,
<<<<<<< HEAD
                color = Color.Gray,
=======
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color.White else Color.Black,
>>>>>>> 964ebc6 (Initial commit)
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
<<<<<<< HEAD
                }
            )
        }

        // 👉 화면 위에 떠야 하므로 Box 안쪽에서 오버레이로 띄우기
=======
                },
                isDarkTheme = isDarkTheme
            )
        }

>>>>>>> 964ebc6 (Initial commit)
        selectedFacility?.let { facility ->
            FacilityDetailOverlayCard(
                facility = facility,
                onDismiss = { selectedFacility = null },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
