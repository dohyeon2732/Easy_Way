package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile_project_3.Navigation.Screen
import com.example.mobile_project_3.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 140.dp,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                // 💡 높이 강제 부여
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f) // 높이 고정 (90% 화면)
                ) {
                    FacilityList()
                }
            },
            modifier = Modifier
                .fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SearchBar(
                    onSearchClick = { query ->
                        println("검색어: $query")
                        scope.launch { sheetState.bottomSheetState.expand() }
                    },
                    onFilterClick = {
                        println("필터 클릭")
                    }
                )

                // 지도
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFFD6F1FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🗺️ 지도 들어갈 자리")
                }
            }
        }

        // 🧷 하단바를 Box의 마지막에 배치해서 가장 위에 렌더링되도록
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavigationBar(navController)
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.home), contentDescription = "홈") },
            label = { Text("홈") },
            selected = true,
            onClick = { navController.navigate(Screen.Home.route) }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.star_t), contentDescription = "즐겨찾기") },
            label = { Text("즐겨찾기") },
            selected = false,
            onClick = { navController.navigate(Screen.Favorites.route) }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.person), contentDescription = "마이페이지") },
            label = { Text("마이페이지") },
            selected = false,
            onClick = { navController.navigate(Screen.MyPage.route) }
        )
    }
}

@Preview
@Composable
private fun Pre_HomeScreen() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}
