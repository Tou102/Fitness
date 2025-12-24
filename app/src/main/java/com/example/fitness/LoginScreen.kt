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
import com.example.fitness.entity.AppRepository
import com.example.fitness.viewModel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Image(
        painter = painterResource(id = R.drawable.back1), // Ensure the image is correct
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    // UI structure
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
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Username input field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    text = "Tên đăng nhập",
                    color = Color.Black,  // Label color in black
                    fontWeight = FontWeight.Bold,  // Bold font for the label
                    fontSize = 16.sp  // Increased font size for better visibility
                )
            },
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF0288D1), RoundedCornerShape(16.dp))
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password input field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = "Mật khẩu",
                    color = Color.Black,  // Label color in black
                    fontWeight = FontWeight.Bold,  // Bold font for the label
                    fontSize = 16.sp  // Increased font size for better visibility
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF0288D1), RoundedCornerShape(16.dp))
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = {
                isLoginEnabled = false
                userViewModel.viewModelScope.launch {
                    val user = userViewModel.loginUser(username, password)
                    if (user != null) {
                        // Báo cho Repository biết Username nào vừa đăng nhập
                        com.example.fitness.entity.AppRepository.currentUserKey = user.username
                        AppRepository.loadUserData()
                        userViewModel.loadUserByUsername(user.username)
                        Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        navController.navigate("gioithieu") // Navigate to the main screen
                    } else {
                        Toast.makeText(context, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                        isLoginEnabled = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            enabled = isLoginEnabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
        ) {
            Text("Đăng nhập", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Link to registration screen
        TextButton(onClick = { navController.navigate("register") }) {
            Text(
                text = "Chưa có tài khoản? Đăng ký ngay!",
                color = Color(0xFF0288D1),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

