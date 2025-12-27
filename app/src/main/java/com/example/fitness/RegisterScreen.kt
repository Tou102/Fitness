package com.example.fitness

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitness.viewModel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- TRẠNG THÁI NHẬP LIỆU ---
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // --- MÀU SẮC (Khai báo trực tiếp để tránh lỗi import) ---
    // Màu xanh dương nhạt -> đậm (Gradient)
    val colorStart = Color(0xFF4FC3F7) // Light Blue
    val colorEnd = Color(0xFF0288D1)   // Dark Blue
    val whiteTransparent = Color.White.copy(alpha = 0.9f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Tạo nền chuyển màu chéo cực đẹp
                brush = Brush.linearGradient(
                    colors = listOf(colorStart, colorEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                // Card trắng bo tròn chứa nội dung
                .clip(RoundedCornerShape(28.dp))
                .background(whiteTransparent)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Text(
                text = "Tạo Tài Khoản",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorEnd
            )

            Text(
                text = "Tham gia cộng đồng Fitness ngay",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // --- INPUT: USERNAME ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên đăng nhập") },
                placeholder = { Text("Ví dụ: hungtapgym") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = colorEnd) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorEnd,
                    focusedLabelColor = colorEnd,
                    cursorColor = colorEnd
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT: PASSWORD ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = colorEnd) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Show Password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorEnd,
                    focusedLabelColor = colorEnd,
                    cursorColor = colorEnd
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT: CONFIRM PASSWORD ---
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Nhập lại mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = colorEnd) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Show Password"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorEnd,
                    focusedLabelColor = colorEnd,
                    cursorColor = colorEnd
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- BUTTON REGISTER ---
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isProcessing = true
                    scope.launch {
                        try {
                            // Gọi hàm đăng ký từ ViewModel
                            userViewModel.registerUser(username, password)
                            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack() // Về màn hình đăng nhập
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            isProcessing = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorEnd
                ),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "ĐĂNG KÝ NGAY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LINK LOGIN ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Đã có tài khoản?", fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = { navController.popBackStack() }) {
                    Text(
                        text = "Đăng nhập",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorEnd
                    )
                }
            }
        }
    }
}