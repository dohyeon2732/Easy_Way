package com.example.mobile_project_3.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project_3.data.FacilityApi
import com.example.mobile_project_3.data.FacilityApi.fetchEvalInfoByFacilityId
import com.example.mobile_project_3.data.FacilityCsvSearcher
import com.example.mobile_project_3.data.parseEvalXml
import com.naver.maps.geometry.LatLng
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

    // ✅ 지도 중심 좌표만 기억
    private val _cameraCenterLatLng = MutableStateFlow(LatLng(37.5408, 127.0793)) // 서울
    val cameraCenterLatLng: StateFlow<LatLng> get() = _cameraCenterLatLng

    fun bringFacilityToTop(facility: FacilityData) {
        val current = _facilities.value.toMutableList()
        current.removeAll { it.wlfctlId == facility.wlfctlId }
        current.add(0, facility)
        _facilities.value = current
        updateFilteredFacilities()
    }

    fun appendFacilities(newList: List<FacilityData>) {
        val favoriteMap = _favoriteFacilities.value.associateBy { it.wlfctlId }
        val current = _facilities.value.toMutableList()

        newList.forEach { newItem ->
            // 이미 동일 ID가 있으면 스킵
            if (current.none { it.wlfctlId == newItem.wlfctlId }) {
                current.add(
                    newItem.copy(isFavorite = favoriteMap.containsKey(newItem.wlfctlId))
                )
            }
        }

        _facilities.value = current
        updateFilteredFacilities()
    }

    fun loadNearbyFacilities(context: Context) {
        viewModelScope.launch {
            val center = cameraCenterLatLng.value
            val nearby = FacilityCsvSearcher.searchFacilitiesNearPosition(context, center, limit = 5)

            val detailed = nearby.map { item ->
                async {
                    try {
                        val xml = fetchEvalInfoByFacilityId("wfcltId", item.welfacilityId)
                        val eval = parseEvalXml(xml)
                        val evalList = eval.evalInfo.split(",").map { it.trim() }

                        FacilityData(
                            faclNm = item.name,
                            wlfctlId = item.welfacilityId,
                            evalInfo = evalList,
                            address = item.address,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            isFavorite = false,
                            type = item.type
                        )
                    } catch (e: Exception) {
                        FacilityData(
                            faclNm = item.name,
                            wlfctlId = item.welfacilityId,
                            evalInfo = listOf("정보 없음"),
                            address = item.address,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            isFavorite = false,
                            type = item.type
                        )
                    }
                }
            }.awaitAll()
            appendFacilities(detailed)
        }
    }

    fun setCameraPosition(latLng: LatLng) {
        _cameraCenterLatLng.value = latLng
    }

    fun getCameraPosition(): LatLng {
        return _cameraCenterLatLng.value
    }

    // ✅ 지도 상태 객체는 저장하지 않음 (Compose가 기억함)

    private val _dataLoadedFlag = mutableStateOf(false)
    val isDataLoaded: Boolean get() = _dataLoadedFlag.value

    fun markDataLoaded() {
        _dataLoadedFlag.value = true
    }

    fun consumeDataLoaded(): Boolean {
        return if (_dataLoadedFlag.value) {
            _dataLoadedFlag.value = false
            true
        } else false
    }

    var showFavoritesOnly = mutableStateOf(false)
        private set

    fun toggleFavoritesOnly() {
        showFavoritesOnly.value = !showFavoritesOnly.value
    }

    private val _currentQuery = mutableStateOf("")
    val currentQuery: String get() = _currentQuery.value

    fun updateCurrentQuery(query: String) {
        _currentQuery.value = query
    }

    private val _facilities = MutableStateFlow<List<FacilityData>>(emptyList())
    val facilities: StateFlow<List<FacilityData>> get() = _facilities

    private val _filteredFacilities = MutableStateFlow<List<FacilityData>>(emptyList())
    val filteredFacilities: StateFlow<List<FacilityData>> get() = _filteredFacilities


    private val _favoriteFacilities = MutableStateFlow<List<FacilityData>>(emptyList())
    val favoriteFacilities: StateFlow<List<FacilityData>> get() = _favoriteFacilities

    private val _selectedFilters = mutableStateOf(setOf<Int>())
    val selectedFilters: Set<Int> get() = _selectedFilters.value

    private val _facilityResult = MutableStateFlow("")
    val facilityResult: StateFlow<String> get() = _facilityResult

    private val _searchQuery = MutableStateFlow("서울")
    val searchQuery: StateFlow<String> get() = _searchQuery

    fun setSearchQuery(newQuery: String) {
        if (newQuery != _searchQuery.value) {
            _searchQuery.value = newQuery
            markDataLoaded()
        }
    }

    fun setFacilities(newList: List<FacilityData>) {
        val favoriteMap = _favoriteFacilities.value.associateBy { it.wlfctlId }
        _facilities.value = newList.map { item ->
            item.copy(isFavorite = favoriteMap.containsKey(item.wlfctlId))
        }
        updateFilteredFacilities()
    }

    fun setSelectedFilters(filters: Set<Int>) {
        _selectedFilters.value = filters
        updateFilteredFacilities()
    }

    fun toggleFavorite(facility: FacilityData) {
        _facilities.value = _facilities.value.map {
            if (it.wlfctlId == facility.wlfctlId) {
                val updated = it.copy(isFavorite = !it.isFavorite)
                if (updated.isFavorite) {
                    userViewModel.addFavorite(updated.wlfctlId) {
                        if (!it) Log.e("FAVORITE", "🔥 Firebase 즐겨찾기 추가 실패")
                    }
                } else {
                    userViewModel.removeFavorite(updated.wlfctlId) {
                        if (!it) Log.e("FAVORITE", "❌ Firebase 즐겨찾기 삭제 실패")
                    }
                }
                updated
            } else it
        }

        val currentFavs = _favoriteFacilities.value.toMutableList()
        if (currentFavs.any { it.wlfctlId == facility.wlfctlId }) {
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

    fun setFavoritesFromIds(context: Context, ids: Set<String>) {
        viewModelScope.launch {
            val facilityItems = FacilityCsvSearcher.searchFacilitiesByIds(context, ids)
            val favoriteList = facilityItems.map { item ->
                async {
                    try {
                        val xml = fetchEvalInfoByFacilityId("wfcltId", item.welfacilityId)
                        val eval = parseEvalXml(xml)
                        FacilityData(
                            faclNm = item.name,
                            type = item.type,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            address = item.address,
                            wlfctlId = item.welfacilityId,
                            evalInfo = eval.evalInfo.split(",").map { it.trim() },
                            isFavorite = true
                        )
                    } catch (e: Exception) {
                        Log.e("FAVORITE_API", "📄 평가 정보 실패: ${item.welfacilityId}", e)
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
            _facilities.value = _facilities.value.map { f ->
                f.copy(isFavorite = ids.contains(f.wlfctlId))
            }
            updateFilteredFacilities()
        }
    }

    private fun updateFilteredFacilities() {
        val all = _facilities.value
        _filteredFacilities.value = if (_selectedFilters.value.isEmpty()) {
            all
        } else {
            all.filter { facility ->
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
                    val normalized = keyword.replace(" ", "").lowercase()
                    facility.evalInfo.any {
                        it.replace(" ", "").lowercase().contains(normalized)
                    }
                }
            }
        }
        Log.d("FILTER_DEBUG", "필터 적용 후 ${_filteredFacilities.value.size}개")
    }

    fun searchFacilities(query: String) {
        viewModelScope.launch {
            try {
                val xml = FacilityApi.fetchByFacilityName(query)
                val result = parseFacilityXml(xml)
                val items = result.items
                _facilityResult.value = if (items.isNotEmpty()) {
                    "총 ${result.totalCount}건\n\n" +
                            items.joinToString("\n\n----------\n\n") {
                                "• 시설명: ${it.name}\n• 유형: ${it.type}\n• 주소: ${it.address}\n• 상태: ${it.businessStatus}\n• 설립일: ${it.estbDate}"
                            }
                } else {
                    "검색 결과 없음"
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
                val result = parseFacilityXml(xml)
                val items = result.items
                _facilityResult.value = if (items.isNotEmpty()) {
                    "총 ${result.totalCount}건\n\n" +
                            items.joinToString("\n\n----------\n\n") {
                                "• 시설명: ${it.name}\n• 유형: ${it.type}\n• 주소: ${it.address}\n• 상태: ${it.businessStatus}\n• 설립일: ${it.estbDate}"
                            }
                } else {
                    "검색 결과 없음"
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
                    if (xml.length > 3000) xml.take(3000) + "\n(이하 생략)"
                    else xml
                } else {
                    "검색 결과 없음"
                }
            } catch (e: Exception) {
                _facilityResult.value = "오류 발생: ${e.localizedMessage}"
            }
        }
    }
}
