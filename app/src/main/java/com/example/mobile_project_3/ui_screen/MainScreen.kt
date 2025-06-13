package com.example.mobile_project_3.ui_screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.mobile_project_3.Navigation.MainNavGraph
import com.example.mobile_project_3.data.FacilityCsvSearcher
import com.example.mobile_project_3.viewmodel.FacilityData
import com.example.mobile_project_3.viewmodel.FacilityViewModel
import com.example.mobile_project_3.viewmodel.UserViewModel

@Composable
fun MainScreen(userViewModel: UserViewModel) {

    val navController = rememberNavController()
    val facilityViewModel = remember { FacilityViewModel(userViewModel) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val initialKeyword = "서울"
        val rawList = FacilityCsvSearcher.searchFacilitiesByKeyword(context, initialKeyword).take(10)

        val facilityList = rawList.map { item ->
            try {
                val xml = com.example.mobile_project_3.data.FacilityApi.fetchEvalInfoByFacilityId("wfcltId", item.welfacilityId)
                val eval = com.example.mobile_project_3.data.parseEvalXml(xml)
                val evalList = eval.evalInfo.split(",").map { it.trim() }

                FacilityData(
                    faclNm = item.name,
                    type = item.type,
                    latitude = item.latitude,
                    longitude = item.longitude,
                    address = item.address,
                    wlfctlId = item.welfacilityId,
                    evalInfo = evalList
                )
            } catch (e: Exception) {
                Log.e("MainScreen", "❌ API 오류 (${item.welfacilityId}): ${e.message}")
                FacilityData(
                    faclNm = item.name,
                    type = item.type,
                    latitude = item.latitude,
                    longitude = item.longitude,
                    address = item.address,
                    wlfctlId = item.welfacilityId,
                    evalInfo = listOf("정보 없음")
                )
            }
        }

        facilityViewModel.setFacilities(facilityList)
        facilityViewModel.markDataLoaded() // ✅ 인자 제거
        facilityViewModel.updateCurrentQuery(initialKeyword)

        userViewModel.loadFavoritesFromFirebase { favoriteIds ->
            Log.d("MainScreen", "🔥 불러온 즐겨찾기 ID들: $favoriteIds")
            facilityViewModel.setFavoritesFromIds(context, favoriteIds)
            Log.d("MainScreen", "✅ 적용된 즐겨찾기: ${facilityViewModel.favoriteFacilities.value.map { it.faclNm }}")
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavGraph(navController, facilityViewModel)
        }
    }
}
