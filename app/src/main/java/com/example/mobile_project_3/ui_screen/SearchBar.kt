package com.example.mobile_project_3.ui_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_project_3.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit,  // 검색 버튼 클릭 시 전달
    onFilterClick: () -> Unit         // 필터 버튼 클릭 시 전달
) {
    var query by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // 높이 살짝 키움
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp) // 여유 있는 padding
            ) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            "시설이나 지역을 검색하세요!",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            lineHeight = 5.sp
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp // 👈 입력되는 텍스트도 동일 크기로
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(
                            horizontal = 0.dp, vertical = 0.dp
                        ),
                    shape = RoundedCornerShape(0.dp),
                )
                // ✅ 이미지 아이콘으로 변경
                IconButton(
                    onClick = { println(onSearchClick(query)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.btn_search),
                        contentDescription = "검색",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // ℹ️ 필터 설정 안내
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "아직 필터가 설정되지 않았어요\n필터를 설정해보세요!",
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )
            TextButton(onClick = { onFilterClick() }) {
                Text("필터 설정하기")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    SearchBar(
        onSearchClick = { query -> println("🔍 검색어: $query") },
        onFilterClick = { println("⚙️ 필터 설정 클릭됨") }
    )
}