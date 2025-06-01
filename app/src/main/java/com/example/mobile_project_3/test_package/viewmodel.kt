package com.example.mobile_project_3.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project_3.data.FacilityApi
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

class FacilityViewModel : ViewModel() {

    private val _facilityResult = MutableStateFlow("")
    val facilityResult: StateFlow<String> get() = _facilityResult

    private val _searchQuery = MutableStateFlow("서울")
    val searchQuery: StateFlow<String> get() = _searchQuery

    fun setSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
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


    /*fun setFacilities(newList: List<FacilityData>) {
        val prevList = _facilities.value
        _facilities.value = newList.map { newItem ->
            val old = prevList.find { it.wlfctlId == newItem.wlfctlId }
            if (old?.isFavorite == true) newItem.copy(isFavorite = true) else newItem
        }
        updateFilteredFacilities()
    }*/

    fun setFacilities(newList: List<FacilityData>) {
        val favoriteMap = _favoriteFacilities.value.associateBy { it.wlfctlId }

        _facilities.value = newList.map { item ->
            val isFav = favoriteMap.containsKey(item.wlfctlId)
            item.copy(isFavorite = isFav)
        }

        updateFilteredFacilities()
    }

    /*fun toggleFavorite(facility: FacilityData) {
        _facilities.value = _facilities.value.map {
            if (it.wlfctlId == facility.wlfctlId) {
                it.copy(isFavorite = !it.isFavorite)
            } else it
        }
        updateFilteredFacilities()
    }*/

    fun toggleFavorite(facility: FacilityData) {
        _facilities.value = _facilities.value.map {
            if (it.wlfctlId == facility.wlfctlId) {
                val updated = it.copy(isFavorite = !it.isFavorite)
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

                _selectedFilters.value.any { index ->
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
