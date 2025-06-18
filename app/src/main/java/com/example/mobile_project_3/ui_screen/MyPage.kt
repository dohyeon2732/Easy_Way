package com.example.mobile_project_3.ui_screen

<<<<<<< HEAD
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun MyPage(navController: NavController) {
    
}
=======
import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import com.example.mobile_project_3.viewmodel.UserViewModel
import com.example.mobile_project_3.Navigation.Screen

@Composable
fun MyPage(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    userViewModel: UserViewModel
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkTheme) Color(0xFF3c3c3c) else Color.White
            )
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("마이페이지", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        Spacer(modifier = Modifier.height(24.dp))

        Text("모드 변경", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F8FA), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(24.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            ModeButton(
                text = "라이트 모드",
                selected = !isDarkTheme,
                modifier = Modifier.weight(1f),
                onClick = { onThemeChange(false) }
            )
            ModeButton(
                text = "다크 모드",
                selected = isDarkTheme,
                modifier = Modifier.weight(1f),
                onClick = { onThemeChange(true) }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        Spacer(modifier = Modifier.height(24.dp))

        Text("비밀번호 변경", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))

        Text("새 비밀번호")
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("입력해주세요.", color = Color(0xFFB0B0B0)) },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isDarkTheme) Color.White else Color(0xFFF5F5F5),
                unfocusedContainerColor = if (isDarkTheme) Color.White else Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = if (isDarkTheme) Color(0xFF5F6368) else Color.Black,
                focusedTextColor = if (isDarkTheme) Color(0xFF5F6368) else Color.Black,
                unfocusedTextColor = if (isDarkTheme) Color(0xFF5F6368) else Color.Black
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("비밀번호 확인")
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text("다시 입력해주세요.", color = Color(0xFFB0B0B0)) },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isDarkTheme) Color.White else Color(0xFFF5F5F5),
                unfocusedContainerColor = if (isDarkTheme) Color.White else Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = if (isDarkTheme) Color(0xFF5F6368) else Color.Black,
                focusedTextColor = if (isDarkTheme) Color(0xFF5F6368) else Color.Black,
                unfocusedTextColor = if (isDarkTheme) Color(0xFF5F6368) else Color.Black
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    password.isBlank() || confirmPassword.isBlank() -> {
                        Toast.makeText(context, "비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    password != confirmPassword -> {
                        Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                    password.length < 6 -> {
                        Toast.makeText(context, "비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        userViewModel.changePassword(password) { success, message ->
                            if (success) {
                                Toast.makeText(context, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                password = ""
                                confirmPassword = ""
                            } else {
                                Toast.makeText(context, message ?: "비밀번호 변경 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30C4CC))
        ) {
            Text("비밀번호 변경하기", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Divider(modifier = Modifier.padding(top = 24.dp), color = Color(0xFFEEEEEE), thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // 로그아웃
        Text("로그아웃", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))
        ActionButton("로그아웃") {
            userViewModel.logout {
                Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                val activity = (context as? Activity)
                activity?.finish()
                activity?.let {
                    context.startActivity(it.intent)
                }
            }
        }

        Divider(modifier = Modifier.padding(top = 24.dp), color = Color(0xFFEEEEEE), thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // 회원탈퇴
        Text("회원탈퇴", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))
        ActionButton("회원탈퇴") {
            userViewModel.deleteAccount { success, message ->
                if (success) {
                    Toast.makeText(context, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    val activity = (context as? Activity)
                    activity?.finish()
                    activity?.let {
                        context.startActivity(it.intent)
                    }
                } else {
                    Toast.makeText(context, message ?: "회원탈퇴 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun ModeButton(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFD2EDFF) else Color.White,
            contentColor = if (selected) Color.Black else Color(0xFFB0B0B0)
        ),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        elevation = null
    ) {
        Text(text)
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30C4CC))
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun textFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFFF5F5F5),
    unfocusedContainerColor = Color(0xFFF5F5F5),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    cursorColor = Color.Black
)
>>>>>>> 964ebc6 (Initial commit)
