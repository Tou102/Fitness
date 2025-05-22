package com.example.fitness

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewModelScope
import com.example.fitness.viewModel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Image(
        painter = painterResource(id = R.drawable.back1), // đổi thành tên file ảnh trong res/drawable
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    // UI cấu trúc
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đăng nhập",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,  // đổi màu chữ thành đen
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Trường nhập tên đăng nhập
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Tên đăng nhập") },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFF1565C0), RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Trường nhập mật khẩu
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFF1565C0), RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Nút Đăng nhập
        Button(
            onClick = {
                isLoginEnabled = false
                userViewModel.viewModelScope.launch {
                    val user = userViewModel.loginUser(username, password)
                    if (user != null) {
                        Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        navController.navigate("gioithieu") // Điều hướng tới màn hình chính
                    } else {
                        Toast.makeText(context, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                        isLoginEnabled = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isLoginEnabled
        ) {
            Text("Đăng nhập")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Liên kết tới màn hình Đăng ký
        TextButton(onClick = { navController.navigate("register") }) {
            Text(
                text = "Chưa có tài khoản? Đăng ký ngay!",
                color = Color.Black
            )
        }
    }
}
