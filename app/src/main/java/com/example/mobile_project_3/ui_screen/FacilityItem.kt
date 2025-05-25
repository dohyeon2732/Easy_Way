package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_project_3.R

data class FacilityData(
    val faclNm: String,
    val wlfctlId: String,
    val evalInfo: List<String>, // 예: ["휠체어가능", "입구 평움"]
    val address: String,
    val isFavorite: Boolean = false // <- 즐겨찾기 여부 추가

)
@Composable
fun FacilityListItem(facility: FacilityData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = R.drawable.sampleimg), // 실제 이미지 있으면 교체
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(facility.faclNm, fontWeight = FontWeight.Bold)
                Text(facility.address, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    facility.evalInfo.forEach {
                        Chip(text = it)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
            if(facility.isFavorite) {
                Icon(
                    painter = painterResource(id = R.drawable.stars),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun Chip(text: String) {
    Surface(
        color = Color(0xFFE0F2F1),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
