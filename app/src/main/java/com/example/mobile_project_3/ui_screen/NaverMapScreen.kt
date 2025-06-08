package com.example.mobile_project_3.ui_screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.example.mobile_project_3.viewmodel.FacilityData
import com.example.mobile_project_3.viewmodel.FacilityViewModel
import com.naver.maps.geometry.LatLng
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
    viewModel: FacilityViewModel
) {
    val fallback = LatLng(37.5408, 127.0793)
    /*val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(fallback, 13.0)
    }*/
    val cameraPositionState = rememberCameraPositionState()


    LaunchedEffect(facilities) {
        Log.d("MAP_SCREEN", "📍 마커 찍을 시설 수: ${facilities.size}")

        facilities.forEachIndexed { index, facility ->
            Log.d(
                "MAP_SCREEN",
                "$index: ${facility.faclNm} / lat=${facility.latitude}, lng=${facility.longitude}"
            )
        }

        // 유효한 한국 좌표인지 검증
       val firstValidLatLng = facilities.firstOrNull {
            val lat = it.latitude.toDoubleOrNull()
            val lng = it.longitude.toDoubleOrNull()
            lat != null && lng != null &&
                    lat in 33.0..39.0 &&
                    lng in 124.0..132.0
        }?.let {
            LatLng(it.latitude.toDouble(), it.longitude.toDouble())
        }

        if (firstValidLatLng != null) {
            cameraPositionState.move(CameraUpdate.scrollTo(firstValidLatLng))
            // 👉 ViewModel에도 저장해두면 다음 화면 진입 시 기억 가능
            viewModel.setCameraPosition(firstValidLatLng)
        }
    }
    NaverMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        facilities.forEach { facility ->
            val lat = facility.latitude.toDoubleOrNull()
            val lng = facility.longitude.toDoubleOrNull()

            if (lat != null && lng != null &&
                lat in 33.0..39.0 && lng in 124.0..132.0
            ) {
                val position = LatLng(lat, lng)

                // 🎯 key를 줘서 마커가 강제로 리컴포즈되게 함
                key(facility.wlfctlId) {
                    Log.d("MAP_SCREEN", "📍 마커 ${facility.faclNm} -> lat=$lat, lng=$lng")
                    Marker(
                        state = rememberMarkerState(position = position),
                        captionText = facility.faclNm
                    )
                }
            }
        }
    }
}