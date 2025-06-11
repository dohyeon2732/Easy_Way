package com.example.mobile_project_3.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project_3.data.FacilityApi
import com.example.mobile_project_3.data.FacilityApi.fetchEvalInfoByFacilityId
import com.example.mobile_project_3.data.FacilityCsvSearcher
import com.example.mobile_project_3.data.parseEvalXml
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.location.FusedLocationSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import parseFacilityXml

data class FacilityData(
    val faclNm: String,
    val wlfctlId: String,
    val evalInfo: List<String>,
    val address: String,
    val latitude: String,
    val longitude: String,
    val isFavorite: Boolean = false,
    val type: String,
)

class FacilityViewModel(private val userViewModel: UserViewModel) : ViewModel() {

    var cameraPositionState: CameraPositionState? by mutableStateOf(null)
    var locationSource: FusedLocationSource? by mutableStateOf(null)

    fun updateCameraPositionState(state: CameraPositionState) {
        cameraPositionState = state
    }
    fun updateLocationSource(source: FusedLocationSource) {
        locationSource = source
    }

    var showFavoritesOnly = mutableStateOf(false)
        private set

    fun toggleFavoritesOnly() {
        showFavoritesOnly.value = !showFavoritesOnly.value
    }

    fun setFavoritesFromIds(context: Context, ids: Set<String>) {
        viewModelScope.launch {
            val facilityItems = FacilityCsvSearcher.searchFacilitiesByIds(context, ids)
            val favoriteList = facilityItems.map { item ->
                async {
                    try {
                        val xml = fetchEvalInfoByFacilityId("wfcltId", item.welfacilityId)
                        val eval = parseEvalXml(xml)
                        val evalList = eval.evalInfo.split(",").map { it.trim() }

                        FacilityData(
                            faclNm = item.name,
                            type = item.type,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            address = item.address,
                            wlfctlId = item.welfacilityId,
                            evalInfo = evalList,
                            isFavorite = true
                        )
                    } catch (e: Exception) {
                        Log.e("FAVORITE_API", "📄 평가 정보 조회 실패: ${item.welfacilityId}", e)
                        FacilityData(
                            faclNm = item.name,
                            type = item.type,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            address = item.address,
                            wlfctlId = item.welfacilityId,
                            evalInfo = listOf("정보 없음"),
                            isFavorite = true
                        )
                    }
                }
            }.awaitAll()

            _favoriteFacilities.value = favoriteList

            // 기존 facilities 목록에서 즐겨찾기 반영
            _facilities.value = _facilities.value.map { facility ->
                facility.copy(isFavorite = ids.contains(facility.wlfctlId))
            }

            updateFilteredFacilities()
        }
    }
    private var _lastLoadedQuery: String? = null
    val lastLoadedQuery: String? get() = _lastLoadedQuery
    private val _isDataLoaded = mutableStateOf(false)
    val isDataLoaded: Boolean get() = _isDataLoaded.value

    fun markDataLoaded(query: String) {
        _lastLoadedQuery = query
        _isDataLoaded.value = true
    }

    fun resetDataLoaded() {
        _isDataLoaded.value = false
        _lastLoadedQuery = null
    }

    private val _currentQuery = mutableStateOf("")
    val currentQuery: String get() = _currentQuery.value

    fun updateCurrentQuery(query: String) {
        _currentQuery.value = query
    }



    val cameraState = MutableStateFlow(CameraPosition(LatLng(37.5408, 127.0793), 13.0))
    //초기 값

    fun setCameraPosition(latLng: LatLng) {
        cameraState.value = CameraPosition(latLng, 13.0)
    }

    private val _facilityResult = MutableStateFlow("")
    val facilityResult: StateFlow<String> get() = _facilityResult

    private val _searchQuery = MutableStateFlow("서울")
    val searchQuery: StateFlow<String> get() = _searchQuery

    fun setSearchQuery(newQuery: String) {
        if (newQuery == _searchQuery.value) return
        _searchQuery.value = newQuery
        resetDataLoaded() // 새 쿼리니까 다시 로딩하도록 설정
    }

    private val _facilities = MutableStateFlow<List<FacilityData>>(emptyList())
    val facilities: StateFlow<List<FacilityData>> get() = _facilities

    private val _filteredFacilities = MutableStateFlow<List<FacilityData>>(emptyList())
    val filteredFacilities: StateFlow<List<FacilityData>> get() = _filteredFacilities

    private val _selectedFilters = mutableStateOf(setOf<Int>())
    val selectedFilters: Set<Int> get() = _selectedFilters.value

    private val MAX_RESULT_LENGTH = 3000


    private val _favoriteFacilities = MutableStateFlow<List<FacilityData>>(emptyList())
    val favoriteFacilities: StateFlow<List<FacilityData>> get() = _favoriteFacilities

    fun setFacilities(newList: List<FacilityData>) {
        val favoriteMap = _favoriteFacilities.value.associateBy { it.wlfctlId }

        _facilities.value = newList.map { item ->
            val isFav = favoriteMap.containsKey(item.wlfctlId)
            item.copy(isFavorite = isFav)
        }
        updateFilteredFacilities()
    }

    fun toggleFavorite(facility: FacilityData) {
        _facilities.value = _facilities.value.map {
            if (it.wlfctlId == facility.wlfctlId) {
                val updated = it.copy(isFavorite = !it.isFavorite)

                if (updated.isFavorite) {
                    userViewModel.addFavorite(updated.wlfctlId) { success ->
                        if (!success) {
                            Log.e("FAVORITE", "🔥 Firebase 즐겨찾기 추가 실패")
                        }
                    }
                } else {
                    userViewModel.removeFavorite(updated.wlfctlId) { success ->
                        if (!success) {
                            Log.e("FAVORITE", "❌ Firebase 즐겨찾기 삭제 실패")
                        }
                    }
                }
                updated
            } else it
        }
        // favoriteFacilities 동기화
        val currentFavs = _favoriteFacilities.value.toMutableList()
        val exists = currentFavs.any { it.wlfctlId == facility.wlfctlId }
        if (exists) {
            currentFavs.removeAll { it.wlfctlId == facility.wlfctlId }
        } else {
            currentFavs.add(facility.copy(isFavorite = true))
        }

        _favoriteFacilities.value = currentFavs

        updateFilteredFacilities()
    }

    fun getFavorites(): List<FacilityData> {
        return _facilities.value.filter { it.isFavorite }
    }

    fun setSelectedFilters(filters: Set<Int>) {
        _selectedFilters.value = filters
        updateFilteredFacilities()
    }

    private fun updateFilteredFacilities() {
        val all = _facilities.value

        _filteredFacilities.value = if (_selectedFilters.value.isEmpty()) {
            all
        } else {
            all.filter { facility ->
                Log.d("EVAL_INFO", "시설명: ${facility.faclNm}, 평가정보: ${facility.evalInfo}")

                _selectedFilters.value.all { index ->
                    val keyword = when (index) {
                        0 -> "주출입구 접근로"
                        1 -> "주출입구 높이차이 제거"
                        2 -> "주출입구(문)"
                        3 -> "승강기"
                        4 -> "장애인전용주차구역"
                        5 -> "장애인사용가능화장실"
                        6 -> "장애인사용가능객실"
                        7 -> "유도 및 안내 설비"
                        else -> ""
                    }
                    val normalizedKeyword = keyword.replace(" ", "").lowercase()
                    facility.evalInfo.any {
                        it.replace(" ", "").lowercase().contains(normalizedKeyword)
                    }
                }
            }
        }

        Log.d("FILTER_DEBUG", "필터 적용 후 시설 개수: ${_filteredFacilities.value.size}")
    }

    fun searchFacilities(query: String) {
        viewModelScope.launch {
            try {
                val xml = FacilityApi.fetchByFacilityName(query)
                val result = parseFacilityXml(xml)
                val items = result.items

                _facilityResult.value = if (items.isNotEmpty()) {
                    "총 ${result.totalCount}건의 결과가 있습니다.\n\n" +
                            items.joinToString("\n\n----------\n\n") { item ->
                                """
                                • 시설명: ${item.name}
                                • 유형: ${item.type}
                                • 주소: ${item.address}
                                • 영업상태: ${item.businessStatus}
                                • 설립일자: ${item.estbDate}
                                """.trimIndent()
                            }
                } else {
                    "검색 결과가 없습니다."
                }

            } catch (e: Exception) {
                _facilityResult.value = "오류 발생: ${e.localizedMessage}"
            }
        }
    }

    fun searchFacilities_type(query: String) {
        viewModelScope.launch {
            try {
                val xml = FacilityApi.fetchByFacilityTypeRaw(query)
                Log.d("FacilityXML", xml)
                val result = parseFacilityXml(xml)
                val items = result.items

                _facilityResult.value = if (items.isNotEmpty()) {
                    "총 ${result.totalCount}건의 결과가 있습니다.\n\n" +
                            items.joinToString("\n\n----------\n\n") { item ->
                                """
                                • 시설명: ${item.name}
                                • 유형: ${item.type}
                                • 주소: ${item.address}
                                • 영업상태: ${item.businessStatus}
                                • 설립일자: ${item.estbDate}
                                """.trimIndent()
                            }
                } else {
                    "검색 결과가 없습니다."
                }

            } catch (e: Exception) {
                _facilityResult.value = "오류 발생: ${e.localizedMessage}"
            }
        }
    }

    fun searchRawXml(query: String) {
        viewModelScope.launch {
            try {
                val xml = FacilityApi.fetchByFacilityTypeRaw(query)

                _facilityResult.value = if (xml.isNotBlank()) {
                    if (xml.length > MAX_RESULT_LENGTH)
                        xml.substring(0, MAX_RESULT_LENGTH) + "\n\n(결과 생략됨...)"
                    else xml
                } else {
                    "검색 결과가 없습니다."
                }

            } catch (e: Exception) {
                _facilityResult.value = "오류 발생: ${e.localizedMessage}"
            }
        }
    }
}
