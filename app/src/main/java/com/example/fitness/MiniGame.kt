package com.example.fitness

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Quiz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.fitness.db.AppDatabase


@Composable
fun MiniGameScreen(
    navController: NavHostController,
    db: AppDatabase? // Thêm hỗ trợ null cho db trong Preview
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Phần đầu - tiêu đề và màu nền xanh đậm với gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()  // Đảm bảo chiếm toàn bộ chiều rộng
                .wrapContentHeight()
                .background(brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF000066), Color(0xFF004D73)) // Gradient từ navy đến đậm
                ))
                .padding(16.dp)
                .border(2.dp, Color.White, RoundedCornerShape(8.dp)), // Tràn viền
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "MINI GAME",
                style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Phần thân - màu nền xanh sáng với gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()  // Đảm bảo chiếm toàn bộ chiều rộng
                .weight(3f)  // Phần này chiếm chiều cao còn lại
                .background(brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0099FF), Color(0xFF66CCFF))
                ))
                .padding(16.dp)
                .border(2.dp, Color.White, RoundedCornerShape(8.dp)), // Tràn viền
        ) {
            // Nội dung game sẽ được thêm vào ở đây

            // Các game sẽ được hiển thị trong các Box con với icon và tên game
            Column {
                GameBox(
                    icon = Icons.Default.Quiz, // Icon cho game đố vui
                    title = "Đố vui",
                    onClick = {
                        navController.navigate("quiz_home")
                    }
                )

            }

            // Nút quay lại nằm trong box 2, dưới cùng trên phần box đó
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()  // Mở rộng nút quay lại toàn bộ chiều ngang
                    .height(60.dp)  // Đảm bảo nút đủ cao
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0288D1))
            ) {
                Text(
                    text = "Quay lại",
                    color = Color.White,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}

@Composable
fun GameBox(
    icon: ImageVector, // Sử dụng ImageVector cho icon
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick)
            .padding(8.dp), // Padding cho mỗi card

        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        // Sử dụng gradient làm màu nền
        backgroundColor = Color.Transparent, // Cho phép nền bên ngoài của Card có màu gradient
        contentColor = Color.White // Đảm bảo rằng text và icon luôn có màu trắng
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3F51B5), Color(0xFF00008B)) // Gradient từ Indigo đến màu xanh đậm
                ))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White // Đảm bảo rằng icon có màu trắng để nổi bật trên nền gradient
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6.copy(color = Color.White), // Màu chữ trắng
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}








