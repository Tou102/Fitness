package com.example.fitness


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.example.fitness.viewModel.UserViewModel
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

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
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Trường nhập tên đăng nhập
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Tên đăng nhập") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Trường nhập mật khẩu
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nút Đăng nhập
        Button(
            onClick = {
                isLoginEnabled = false
                userViewModel.viewModelScope.launch {
                    val user = userViewModel.loginUser(username, password)
                    if (user != null) {
                        Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        navController.navigate("workout") // Điều hướng tới màn hình chính
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

        Spacer(modifier = Modifier.height(8.dp))

        // Liên kết tới màn hình Đăng ký
        TextButton(onClick = { navController.navigate("register") }) {
            Text("Chưa có tài khoản? Đăng ký ngay!")
        }
    }
}