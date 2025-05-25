package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FacilityList() {
    val sampleData = listOf(
        FacilityData(
            faclNm = "건국대학교 신공학관",
            wlfctlId = "4421010800-1-01490001",
            evalInfo = listOf("휠체어가능", "입구 평움"),
            address = "서울시 광진구 신림동 123-123"
        ),
        FacilityData(
            faclNm = "여기는 맛집",
            wlfctlId = "4421010800-1-01490002",
            evalInfo = listOf("주출입구 경사로"),
            address = "서울시 맛동 456-789",
            isFavorite = true
        )
    )

    Column {
        Text("시설 목록", fontSize = 18.sp, modifier = Modifier.padding(16.dp))

        LazyColumn {
            items(sampleData) { item ->
                FacilityListItem(facility = item)
            }
        }
    }
}


@Preview(showBackground = true, name = "시설 리스트 프리뷰")
@Composable
fun PreviewFacilityListScreen() {
    FacilityList()
}
