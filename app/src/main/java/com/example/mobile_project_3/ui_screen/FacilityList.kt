package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.background
<<<<<<< HEAD
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
=======
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobile_project_3.data.FacilityItem
>>>>>>> 964ebc6 (Initial commit)
import com.example.mobile_project_3.viewmodel.FacilityData

@Composable
fun FacilityList(
    facilities: List<FacilityData>,
    showTopOnly: Boolean,
<<<<<<< HEAD
    onUserScroll: () -> Unit,
    onCollapseRequest: () -> Unit,
    onToggleFavorite: (FacilityData) -> Unit,
    onItemClick: (FacilityData) -> Unit
) {
    val listState = rememberLazyListState()

    // 유저 스크롤 시 확장 트리거
    LaunchedEffect(listState.firstVisibleItemIndex, listState.isScrollInProgress) {
        // 맨 위까지 스크롤 됐고, 유저가 더 이상 스크롤하지 않으면 축소
        if (!listState.isScrollInProgress &&
            listState.firstVisibleItemIndex == 0 &&
            !showTopOnly // 이미 확장된 상태에서만
        ) {
            onCollapseRequest() // 이 콜백에서 partialExpand() 호출
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(Color.White),
        state = listState
    ) {
        items(facilities) { item ->
            FacilityListItem(
                facility = item,
                onFavoriteClick = {
                    onToggleFavorite(item)
                },
                onClick = {
                    onItemClick(item)
                }
            )
=======
    onUserScroll: (Boolean) -> Unit,
    onCollapseRequest: () -> Unit,
    onToggleFavorite: (FacilityData) -> Unit,
    onItemClick: (FacilityData) -> Unit,
    isDarkTheme: Boolean
) {
    val lazyListState = rememberLazyListState()
    val isScrolling = remember { mutableStateOf(false) }
    val lastScrollTime = remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    lastScrollTime.value = System.currentTimeMillis()
                    onUserScroll(true)
                } else {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastScrollTime.value > 1000) {
                        onUserScroll(false)
                    }
                }
            }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White)
    ) {
        items(facilities) { facility ->
            Column {
                FacilityListItem(
                    facility = facility,
                    onFavoriteClick = { onToggleFavorite(facility) },
                    onClick = { onItemClick(facility) },
                    isDarkTheme = isDarkTheme
                )
                if (facility != facilities.last()) {
                    Divider(
                        color = Color(0xFFEEEEEE),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
>>>>>>> 964ebc6 (Initial commit)
        }
    }
}