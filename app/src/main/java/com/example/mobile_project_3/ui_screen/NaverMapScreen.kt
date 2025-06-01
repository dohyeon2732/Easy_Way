package com.example.mobile_project_3.ui_screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.mobile_project_3.viewmodel.FacilityData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberMarkerState


@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun NaverMapScreen(
    modifier: Modifier = Modifier,
    facilities: List<FacilityData>,
) {
    val fallback = LatLng(37.5408, 127.0793)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(fallback, 13.0)
    }

    LaunchedEffect(facilities) {
        Log.d("MAP_SCREEN", "📍 마커 찍을 시설 수: ${facilities.size}")
        facilities.forEachIndexed { index, facility ->
            Log.d("MAP_SCREEN", "$index: ${facility.faclNm} / lat=${facility.latitude}, lng=${facility.longitude}")
        }

        // 🔧 여기에 직접 계산하도록 수정
        val firstValidLatLng = facilities.firstNotNullOfOrNull {
            val lat = it.latitude.toDoubleOrNull()
            val lng = it.longitude.toDoubleOrNull()
            if (lat != null && lng != null) LatLng(lat, lng) else null
        }

        if (firstValidLatLng != null) {
            cameraPositionState.move(CameraUpdate.scrollTo(firstValidLatLng))
        }
    }

    NaverMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        facilities.forEach { facility ->
            val lat = facility.latitude.toDoubleOrNull()
            val lng = facility.longitude.toDoubleOrNull()
            if (lat != null && lng != null) {
                Marker(
                    state = rememberMarkerState(position = LatLng(lat, lng)),
                    captionText = facility.faclNm
                )
            }
        }
    }
}