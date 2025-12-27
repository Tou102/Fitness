package com.example.fitness

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitness.viewModel.UserViewModel

@Composable
fun RegisterScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isRegisterEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Màu xanh dương chủ đạo fitness
    val primaryBlue = Color(0xFF0D47A1)          // xanh dương đậm
    val backgroundBlue = Color(0xFFE3F2FD)       // xanh nền nhạt

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBlue)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đăng ký tài khoản",
            style = MaterialTheme.typography.headlineMedium.copy(color = primaryBlue),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Tên đăng nhập", color = primaryBlue) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = primaryBlue,
                unfocusedIndicatorColor = primaryBlue.copy(alpha = 0.5f),
                focusedLabelColor = primaryBlue,
                unfocusedLabelColor = primaryBlue.copy(alpha = 0.7f),
                cursorColor = primaryBlue,
                focusedTextColor = primaryBlue,
                unfocusedTextColor = primaryBlue
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu", color = primaryBlue) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = primaryBlue,
                unfocusedIndicatorColor = primaryBlue.copy(alpha = 0.5f),
                focusedLabelColor = primaryBlue,
                unfocusedLabelColor = primaryBlue.copy(alpha = 0.7f),
                cursorColor = primaryBlue,
                focusedTextColor = primaryBlue,
                unfocusedTextColor = primaryBlue
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Xác nhận mật khẩu", color = primaryBlue) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = primaryBlue,
                unfocusedIndicatorColor = primaryBlue.copy(alpha = 0.5f),
                focusedLabelColor = primaryBlue,
                unfocusedLabelColor = primaryBlue.copy(alpha = 0.7f),
                cursorColor = primaryBlue,
                focusedTextColor = primaryBlue,
                unfocusedTextColor = primaryBlue
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isRegisterEnabled = false
                if (password == confirmPassword) {
                    userViewModel.registerUser(username, password)
                    Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                    isRegisterEnabled = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isRegisterEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
        ) {
            Text(
                text = "Đăng ký",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text(
                text = "Đã có tài khoản? Đăng nhập ngay!",
                color = primaryBlue,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}