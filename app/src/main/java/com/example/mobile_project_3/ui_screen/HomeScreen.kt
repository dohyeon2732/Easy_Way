package com.example.mobile_project_3.ui_screen

import SearchBarWithFilter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_project_3.data.FacilityApi.fetchEvalInfoByFacilityId
import com.example.mobile_project_3.data.FacilityCsvSearcher
import com.example.mobile_project_3.data.parseEvalXml
import com.example.mobile_project_3.viewmodel.FacilityData
import com.example.mobile_project_3.viewmodel.FacilityViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController,viewModel: FacilityViewModel) {
    var isLoading by remember { mutableStateOf(false) }
    var selectedFacility by remember { mutableStateOf<FacilityData?>(null) }
    var selectedTab by remember { mutableStateOf("home") } // or use enum
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showTopOnly by remember { mutableStateOf(true) }


    val context = LocalContext.current

    LaunchedEffect(searchQuery) {

        if (viewModel.isDataLoaded && searchQuery == viewModel.lastLoadedQuery) {
            Log.d("HOME_SCREEN", "🚫 API 재호출 생략됨")
            return@LaunchedEffect
        }

        isLoading = true
        val rawList = FacilityCsvSearcher.searchFacilitiesByKeyword(context, searchQuery)
            .take(10) // 최대 20개

        Log.d("FACILITY_LOG", "🔍 검색 결과 (최대 10개): ${rawList.size}개")

        val chunkedList = rawList.chunked(3) //10개를 3 3 4로 나눔
        val allFacilities = mutableListOf<FacilityData>()

        chunkedList.forEachIndexed { index, chunk ->
            val deferredList = chunk.map { item ->
                async {
                    try {
                        val xml = fetchEvalInfoByFacilityId("wfcltId", item.welfacilityId)
                        val eval = parseEvalXml(xml)
                        val evalList = eval.evalInfo.split(",").map { it.trim() }
                        //정보 시설이 가진 정보 리스트

                        FacilityData(
                            faclNm = item.name,
                            wlfctlId = item.welfacilityId,
                            address = item.address,
                            evalInfo = evalList,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            type = item.type.substringAfter(" - ").trim()
                        )
                    } catch (e: Exception) {
                        Log.e("FACILITY_API", "📄 API 실패: ${item.welfacilityId}", e)
                        FacilityData(
                            faclNm = item.name,
                            wlfctlId = item.welfacilityId,
                            address = item.address,
                            evalInfo = listOf("정보 없음"),
                            latitude = item.latitude,
                            longitude = item.longitude,
                            type = item.type.substringAfter(" - ").trim()
                        )
                    }
                }
            }
            allFacilities += deferredList.awaitAll()
        }
        viewModel.setFacilities(allFacilities)
        viewModel.markDataLoaded(searchQuery) // ✅ 로딩 완료 기록
        isLoading = false
    }

    val filteredList by viewModel.filteredFacilities.collectAsState() // ✅ 스코프를 전체로 확장

    Box(modifier = Modifier.fillMaxSize()
        .background(Color.White)) { // 👈 전체를 감싸는 Box로 바꿈
        Column(modifier = Modifier.fillMaxSize()
            .background(Color.White)) {
            Box(modifier = Modifier.weight(1f)
                .background(Color.White)) {
                BottomSheetScaffold(
                    scaffoldState = sheetState,
                    sheetPeekHeight = if (showTopOnly) 160.dp else 500.dp,
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    containerColor = White,
                    sheetContent = {
                        Column( // ✅ 새로 감싸기
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White) // ✅ 흰색 배경 지정
                        ) {
                            Log.d("UI_DEBUG", "화면에 표시될 시설 수: ${filteredList.size}")
                            FacilityList(
                                facilities = filteredList,
                                showTopOnly = showTopOnly,
                                onUserScroll = {
                                    scope.launch {
                                        showTopOnly = false
                                        sheetState.bottomSheetState.expand()
                                    }
                                },
                                onCollapseRequest = {
                                    scope.launch {
                                        showTopOnly = true
                                        sheetState.bottomSheetState.partialExpand()
                                    }
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onItemClick = { facility ->
                                    scope.launch {
                                        try {
                                            showTopOnly = true
                                            sheetState.bottomSheetState.partialExpand()
                                            selectedFacility =
                                                viewModel.filteredFacilities.value.find { it.wlfctlId == facility.wlfctlId }
                                        } catch (e: Exception) {
                                            Log.e("FACILITY_ERROR", "시설 선택 실패", e)
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color.White)
                    ) {
                        SearchBarWithFilter(
                            onSearchClick = { query ->
                                viewModel.setSearchQuery(query)
                                showTopOnly = true
                                scope.launch {
                                    sheetState.bottomSheetState.partialExpand()
                                }
                            },
                            onFilterApply = { selectedFilterSet ->
                                // 필터 적용 시 ViewModel에서 filtering 기능 구현 가능
                                viewModel.setSelectedFilters(selectedFilterSet)
                                println("적용된 필터: $selectedFilterSet")
                                Log.d("FilterList", "필터된 표시될 시설 수: ${selectedFilterSet}")

                            },
                            viewModel = viewModel
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Log.d("facilites", "화면에 표시될 시설 수: ${filteredList}")
                            NaverMapScreen(facilities = filteredList, viewModel = viewModel)
                            selectedFacility?.let { facility ->
                                FacilityDetailOverlayCard(
                                    facility = facility,
                                    onDismiss = { selectedFacility = null }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ✅ 로딩 인디케이터는 화면 위에 덮어씀
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)), // 반투명 배경
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
    }

}