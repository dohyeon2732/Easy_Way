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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_project_3.data.FacilityApi.fetchEvalInfoByFacilityId
import com.example.mobile_project_3.data.FacilityCsvSearcher
import com.example.mobile_project_3.data.parseEvalXml
import com.example.mobile_project_3.viewmodel.FacilityData
import com.example.mobile_project_3.viewmodel.FacilityViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.location.FusedLocationSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalNaverMapApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: FacilityViewModel, isDarkTheme: Boolean) {
    var isLoading by remember { mutableStateOf(false) }
    var selectedFacility by remember { mutableStateOf<FacilityData?>(null) }
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showTopOnly by remember { mutableStateOf(true) }

    val cameraPositionState = rememberCameraPositionState()
    val locationSource = rememberFusedLocationSource() as FusedLocationSource

    val context = LocalContext.current

    // ✅ ViewModel에 저장된 좌표로 초기 지도 위치 이동
    LaunchedEffect(Unit) {
        val savedLatLng = viewModel.getCameraPosition()
        Log.d("HomeScreen", "📦 초기 진입 → ViewModel 저장 위치: $savedLatLng")
        cameraPositionState.move(CameraUpdate.scrollTo(savedLatLng))
        Log.d("HomeScreen", "📍 지도 초기 위치 이동: $savedLatLng")
    }

    // ✅ 검색어 변경 시 데이터 불러오기 & 지도 이동
    LaunchedEffect(searchQuery) {
        if (!viewModel.consumeDataLoaded()) {
            Log.d("HOME_SCREEN", "🚫 API 재호출 생략됨")
            return@LaunchedEffect
        }

        isLoading = true
        val rawList = FacilityCsvSearcher.searchFacilitiesByKeyword(context, searchQuery).take(10)
        Log.d("FACILITY_LOG", "🔍 검색 결과 (최대 10개): ${rawList.size}개")

        val chunkedList = rawList.chunked(3)
        val allFacilities = mutableListOf<FacilityData>()

        chunkedList.forEach { chunk ->
            val deferredList = chunk.map { item ->
                async {
                    try {
                        val xml = fetchEvalInfoByFacilityId("wfcltId", item.welfacilityId)
                        val eval = parseEvalXml(xml)
                        val evalList = eval.evalInfo.split(",").map { it.trim() }

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

        // ✅ 첫 유효 좌표로 지도 이동 및 저장
        val firstValid = allFacilities.firstOrNull {
            val lat = it.latitude.toDoubleOrNull()
            val lng = it.longitude.toDoubleOrNull()
            lat != null && lng != null && lat in 33.0..39.0 && lng in 124.0..132.0
        }

        firstValid?.let {
            val latLng = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
            Log.d("HomeScreen", "🔍 검색 결과 중 첫 유효 좌표 발견: $latLng")

            cameraPositionState.move(CameraUpdate.scrollTo(latLng))
            Log.d("HomeScreen", "📍 지도 이동 완료 (검색 결과 위치)")

            viewModel.setCameraPosition(latLng)
            Log.d("HomeScreen", "📍 최초 결과 위치로 이동: $latLng")
        }

        isLoading = false
    }

    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.position }
            .distinctUntilChanged()
            .collectLatest { pos ->
                Log.d("CAMERA_MOVE", "📍 지도 이동됨: ${pos.target}")

                // ✅ ViewModel 카메라 중심 저장
                viewModel.setCameraPosition(pos.target)

                // ✅ 새 근처 시설 10개 로드 (중복은 appendFacilities가 막음)
                viewModel.loadNearbyFacilities(context)
            }
    }

    val filteredList by viewModel.filteredFacilities.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White)
        ) {
            Box(
                modifier = Modifier.weight(1f)
                    .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White)
            ) {
                BottomSheetScaffold(
                    scaffoldState = sheetState,
                    sheetPeekHeight = if (showTopOnly) 160.dp else 500.dp,
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    containerColor = if (isDarkTheme) Color(0xFF3c3c3c) else Color.White,
                    sheetContent = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White)
                        ) {
                            Log.d("UI_DEBUG", "표시될 시설 수: ${filteredList.size}")
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
                                        showTopOnly = true
                                        sheetState.bottomSheetState.partialExpand()
                                        selectedFacility = facility
                                    }
                                },
                                isDarkTheme = isDarkTheme
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White)
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
                                viewModel.setSelectedFilters(selectedFilterSet)
                                println("적용된 필터: $selectedFilterSet")
                                Log.d("FilterList", "필터된 표시될 시설 수: ${selectedFilterSet}")

                            },
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            NaverMapScreen(
                                facilities = filteredList,
                                viewModel = viewModel,
                                cameraPositionState = cameraPositionState,
                                locationSource = locationSource
                            )
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

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),

                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
    }

}

