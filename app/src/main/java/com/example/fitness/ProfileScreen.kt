package com.example.fitness

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween // Đẩy phần dưới cùng xuống
    ) {
        // Tiêu đề ở trên cùng
        Text(
            text = "Hồ sơ",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Chỗ này bạn có thể thêm các nội dung hồ sơ như tên, email, v.v.
        Spacer(modifier = Modifier.weight(1f)) // Đẩy phần dưới cùng xuống

        // Nút ở dưới cùng
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Quay về")
            }

            Spacer(modifier = Modifier.width(16.dp)) // Khoảng cách giữa 2 nút

            Button(
                onClick = { /* TODO: Xử lý đăng xuất */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Đăng xuấthhhhh", color = Color.White)
            }
        }
    }
}
