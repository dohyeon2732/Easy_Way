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
<<<<<<< HEAD
=======
import androidx.compose.material3.Divider
>>>>>>> 964ebc6 (Initial commit)
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
<<<<<<< HEAD
=======
import androidx.compose.ui.text.font.FontWeight
>>>>>>> 964ebc6 (Initial commit)
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_project_3.R
import com.example.mobile_project_3.viewmodel.FacilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithFilter(
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit,
    onFilterApply: (Set<Int>) -> Unit,
<<<<<<< HEAD
    viewModel: FacilityViewModel, // ✅ ViewModel 주입
) {
    val query = viewModel.currentQuery // ✅ ViewModel에서 query 가져옴
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedFilters = viewModel.selectedFilters // ✅ 상태 가져오기

    // 🔽 메인 UI
=======
    viewModel: FacilityViewModel,
    isDarkTheme: Boolean = false
) {
    val query = viewModel.currentQuery
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedFilters = viewModel.selectedFilters

>>>>>>> 964ebc6 (Initial commit)
    Column(modifier = modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = {
<<<<<<< HEAD
                        viewModel.updateCurrentQuery(it) // ✅ 입력값 ViewModel에 저장
=======
                        viewModel.updateCurrentQuery(it)
>>>>>>> 964ebc6 (Initial commit)
                    },
                    placeholder = {
                        Text("시설이나 지역을 검색하세요!", color = Color.Gray, fontSize = 12.sp)
                    },
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
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
                        .height(48.dp),
                    shape = RoundedCornerShape(0.dp),
                )
                IconButton(
                    onClick = { onSearchClick(query) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.btn_search),
                        contentDescription = "검색",
                        tint = Color.Unspecified
                    )
                }
            }
        }

<<<<<<< HEAD
        // ℹ️ 필터 안내 및 버튼
=======
>>>>>>> 964ebc6 (Initial commit)
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
<<<<<<< HEAD
                color = Color.Gray
=======
                color = if (isDarkTheme) Color.White else Color.Gray
>>>>>>> 964ebc6 (Initial commit)
            )
            TextButton(onClick = { showFilterSheet = true }) {
                Text("필터 설정하기")
            }
        }
    }

<<<<<<< HEAD
    // 🔽 필터 설정 BottomSheet
=======
>>>>>>> 964ebc6 (Initial commit)
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
<<<<<<< HEAD
            Column(modifier = Modifier.padding(16.dp)) {
                Text("필터 설정", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
=======
            Column(
                modifier = Modifier
                    .background(if (isDarkTheme) Color(0xFF3c3c3c) else Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "필터 설정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (isDarkTheme) Color.White else Color.Black
                )
>>>>>>> 964ebc6 (Initial commit)

                val options = listOf(
                    "주출입구 접근로", "주출입구 높이차이 제거", "주출입구(문)", "승강기",
                    "장애인전용주차구역", "장애인사용가능화장실", "장애인사용가능객실", "유도 및 안내 설비"
                )

                options.forEachIndexed { index, label ->
                    val isSelected = selectedFilters.contains(index)
                    TextButton(
                        onClick = {
                            val updated = selectedFilters.toMutableSet().apply {
                                if (contains(index)) remove(index) else add(index)
                            }
<<<<<<< HEAD
                            viewModel.setSelectedFilters(updated) // ✅ ViewModel로 업데이트
=======
                            viewModel.setSelectedFilters(updated)
>>>>>>> 964ebc6 (Initial commit)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                if (isSelected) Color(0xFF30C4CC) else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(label, color = Color.White)
                    }
                }

<<<<<<< HEAD
                // ✅ 확인 버튼
                TextButton(
=======
                androidx.compose.material3.Button(
>>>>>>> 964ebc6 (Initial commit)
                    onClick = {
                        onFilterApply(selectedFilters)
                        showFilterSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
<<<<<<< HEAD
                ) {
                    Text("확인")
=======
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF30C4CC))
                ) {
                    Text("확인", fontWeight = FontWeight.Bold, color = Color.White)
>>>>>>> 964ebc6 (Initial commit)
                }
            }
        }
    }
}
