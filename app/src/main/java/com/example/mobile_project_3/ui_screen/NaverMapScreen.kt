package com.example.mobile_project_3.ui_screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobile_project_3.R
import com.example.mobile_project_3.viewmodel.FacilityData
import com.example.mobile_project_3.viewmodel.FacilityViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberMarkerState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalNaverMapApi::class)
@Composable
fun NaverMapScreen(
    modifier: Modifier = Modifier,
    facilities: List<FacilityData>,
    viewModel: FacilityViewModel,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource
) {
    val context = LocalContext.current
    val fallback = LatLng(37.5408, 127.0793)

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val granted = permissionState.permissions.all { it.status.isGranted }

    LaunchedEffect(permissionState) {
        permissionState.launchMultiplePermissionRequest()
    }

    val showFavoritesOnly by remember { viewModel.showFavoritesOnly }

    val filteredFacilities = if (showFavoritesOnly) {
        facilities.filter { it.isFavorite }
    } else facilities

    // ✅ 지도 중심 좌표 복원
    LaunchedEffect(Unit) {
        val saved = viewModel.getCameraPosition()
        cameraPositionState.move(CameraUpdate.scrollTo(saved))
        Log.d("position_re", "📍 [Initial Load] moved to saved $saved")
    }

    // ✅ 시설 변경 시 최초 유효 좌표로 이동
    LaunchedEffect(facilities) {
        if (!viewModel.consumeDataLoaded()) {
            Log.d("position_re", "🚫 [facilities] 플래그 소비 실패, 이동 생략")
            return@LaunchedEffect
        }

        val firstValid = facilities.firstOrNull {
            it.latitude.toDoubleOrNull()?.let { lat -> lat in 33.0..39.0 } == true &&
                    it.longitude.toDoubleOrNull()?.let { lng -> lng in 124.0..132.0 } == true
        }

        if (firstValid != null) {
            val target = LatLng(firstValid.latitude.toDouble(), firstValid.longitude.toDouble())
            cameraPositionState.move(CameraUpdate.scrollTo(target))
            viewModel.setCameraPosition(target)
            Log.d("position_re", "📍 [Facilities Load] moved to $target")
        } else {
            Log.d("position_re", "⚠️ [Facilities Load] 유효 좌표 없음")
        }
    }

    // 📦 Box로 지도와 버튼을 감쌈
    Box(modifier = modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            locationSource = locationSource.takeIf { granted },
            properties = MapProperties(
                locationTrackingMode = LocationTrackingMode.None
            ),
            uiSettings = MapUiSettings(isLocationButtonEnabled = true)
        ) {
            filteredFacilities.forEach { facility ->
                val lat = facility.latitude.toDoubleOrNull()
                val lng = facility.longitude.toDoubleOrNull()
                if (lat != null && lng != null && lat in 33.0..39.0 && lng in 124.0..132.0) {
                    val position = LatLng(lat, lng)
                    key(facility.wlfctlId) {
                        Marker(
                            state = rememberMarkerState(position = position),
                            captionText = facility.faclNm,
                            onClick = {
                                cameraPositionState.move(CameraUpdate.scrollTo(position))
                                viewModel.setCameraPosition(position)
                                Log.d("position_re", "📍 [Marker] moved to $position")
                                true
                            }
                        )
                    }
                }
            }
        }

        // 🧭 현재 위치로 이동 버튼
        IconButton(
            onClick = {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val current = if (location != null) {
                        LatLng(location.latitude, location.longitude)
                    } else fallback

                    cameraPositionState.move(CameraUpdate.scrollTo(current))
                    viewModel.setCameraPosition(current)
                    Log.d("position_re", "📍 [Current Location] moved to $current")
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
                .size(56.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.myposition2),
                contentDescription = "현재 위치로 이동",
                modifier = Modifier.fillMaxSize()
            )
        }

        // ⭐ 즐겨찾기 토글 버튼
        IconButton(
            onClick = { viewModel.toggleFavoritesOnly() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 72.dp, end = 8.dp)
                .size(56.dp)
        ) {
            val icon = if (showFavoritesOnly)
                painterResource(id = R.drawable.favorite_star_on)
            else
                painterResource(id = R.drawable.favorite_star_off)

            Image(
                painter = icon,
                contentDescription = "즐겨찾기만 보기 토글",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
