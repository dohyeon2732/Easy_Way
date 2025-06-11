package com.example.mobile_project_3.ui_screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
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


    val showFavoritesOnly = viewModel.showFavoritesOnly.value

    val filteredFacilities = if (showFavoritesOnly) {
        facilities.filter { it.isFavorite }
    } else facilities



    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val context = LocalContext.current
    val fallback = LatLng(37.5408, 127.0793)


    // 권한 요청
    LaunchedEffect(permissionState) {
        permissionState.launchMultiplePermissionRequest()
    }

    val granted = permissionState.permissions.all { it.status.isGranted }

    // 지도 시작 위치 설정
    LaunchedEffect(facilities) {
        val firstValidLatLng = facilities.firstOrNull {
            val lat = it.latitude.toDoubleOrNull()
            val lng = it.longitude.toDoubleOrNull()
            lat != null && lng != null && lat in 33.0..39.0 && lng in 124.0..132.0
        }?.let {
            LatLng(it.latitude.toDouble(), it.longitude.toDouble())
        }

        if (firstValidLatLng != null) {
            cameraPositionState.move(CameraUpdate.scrollTo(firstValidLatLng))
            viewModel.setCameraPosition(firstValidLatLng)
        }
    }

    // 📦 Box로 지도와 버튼을 감쌈
    Box(modifier = modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            locationSource = locationSource.takeIf { granted },
            properties = MapProperties(
                locationTrackingMode = if (granted) LocationTrackingMode.Follow else LocationTrackingMode.None
            ),
            uiSettings = MapUiSettings(
                isLocationButtonEnabled = true
            )
        ) {
            filteredFacilities.forEach { facility -> // 👈 여기 수정
                val lat = facility.latitude.toDoubleOrNull()
                val lng = facility.longitude.toDoubleOrNull()
                if (lat != null && lng != null && lat in 33.0..39.0 && lng in 124.0..132.0) {
                    val position = LatLng(lat, lng)
                    key(facility.wlfctlId) {
                        Marker(
                            state = rememberMarkerState(position = position),
                            captionText = facility.faclNm,
                            onClick = {
                                cameraPositionState.move(CameraUpdate.scrollTo(position)) // ✅ 이동
                                true
                            }
                        )
                    }
                }
            }
        }

        // 🧭 현재 위치로 이동 버튼 (오른쪽 중간)
        IconButton(
            onClick = {
                // 실제 현재 위치로 이동
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val current = LatLng(location.latitude, location.longitude)
                        cameraPositionState.move(CameraUpdate.scrollTo(current))
                    } else {
                        // fallback 이동
                        cameraPositionState.move(CameraUpdate.scrollTo(fallback))
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd) // 👉 오른쪽 중앙
                .padding(top = 8.dp, end = 8.dp)
                .size(56.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.myposition2),
                contentDescription = "현재 위치로 이동",
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = {
                viewModel.toggleFavoritesOnly() // 👈 상태 토글
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 72.dp, end = 8.dp) // 👉 아래로 살짝 떨어뜨리기
                .size(56.dp)
        ) {
            val icon = if (showFavoritesOnly)
                painterResource(id = R.drawable.favorite_star_on) // 즐겨찾기만 보기일 때
            else
                painterResource(id = R.drawable.favorite_star_off) // 전체 보기일 때

            Image(
                painter = icon,
                contentDescription = "즐겨찾기만 보기 토글",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
