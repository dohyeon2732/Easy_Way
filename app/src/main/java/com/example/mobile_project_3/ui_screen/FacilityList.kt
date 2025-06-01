package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mobile_project_3.viewmodel.FacilityData

@Composable
fun FacilityList(
    facilities: List<FacilityData>,
    showTopOnly: Boolean,
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
        }
    }
}