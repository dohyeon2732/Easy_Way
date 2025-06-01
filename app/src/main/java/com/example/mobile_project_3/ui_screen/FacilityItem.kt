package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_project_3.R
import com.example.mobile_project_3.viewmodel.FacilityData


@Composable
fun FacilityListItem(
    facility: FacilityData,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit  // ← 추가
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White) // ✅ 배경 흰색 지정
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // ⬅ 왼쪽: 이미지
            Image(
                painter = painterResource(id = R.drawable.sampleimg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // ➡ 가운데: 정보들
            Column(modifier = Modifier.weight(1f)) {
                Text(facility.faclNm, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(facility.address, fontSize = 12.sp, color = Color.Gray)
                Text("${facility.type}", fontSize = 11.sp, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(4.dp))

            }

            // ⭐ 즐겨찾기 아이콘
            androidx.compose.material3.IconButton(onClick = onFavoriteClick) {
                Icon(
                    painter = painterResource(
                        id = if (facility.isFavorite) R.drawable.stars else R.drawable.star_t
                    ),
                    contentDescription = "즐겨찾기 아이콘",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
@Composable
fun Chip(text: String) {
    Surface(
        color = White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
