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
import com.example.fitness.viewModel.UserViewModel


@Composable
fun RegisterScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isRegisterEnabled by remember { mutableStateOf(true) }
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
            text = "Đăng ký tài khoản",
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

        Spacer(modifier = Modifier.height(8.dp))

        // Trường nhập xác nhận mật khẩu
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Xác nhận mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nút Đăng ký
        Button(
            onClick = {
                isRegisterEnabled = false

                if (password == confirmPassword) {
                    userViewModel.registerUser(username, password)
                    Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    navController.popBackStack() // Quay lại màn hình đăng nhập
                } else {
                    Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                    isRegisterEnabled = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isRegisterEnabled
        ) {
            Text("Đăng ký")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Liên kết tới màn hình Đăng nhập
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Đã có tài khoản? Đăng nhập ngay!")
        }
    }
}
