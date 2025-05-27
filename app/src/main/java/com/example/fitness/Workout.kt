package com.example.fitness

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun WorkoutScreen(navController: NavHostController) {

    val workoutItems = listOf(
        WorkoutItem("FULL BODY", R.drawable.nho, "workoutDetails/Fullbody"),
        WorkoutItem("ABS", R.drawable.nguoi_lon, "workoutDetails/Abs"),
        WorkoutItem("CHEST", R.drawable.download, "workoutDetails/Chest"),
        WorkoutItem("ARM", R.drawable.tapvo, "workoutDetails/Arm"),
    )

    // Sử dụng Box để căn giữa tất cả nội dung
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF2196F3))  // Nền xanh dương đậm
    ) {
        // Tiêu đề nằm ở trên cùng, giữa màn hình
        Text(
            text = "Bài tập",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White, // Chữ trắng nổi bật trên nền xanh
                fontWeight = FontWeight.Bold, // Chữ đậm
                fontSize = 32.sp // Tăng kích thước chữ
            ),
            modifier = Modifier
                .align(Alignment.TopCenter) // Căn giữa theo chiều ngang
                .padding(top = 20.dp) // Giảm khoảng cách từ trên xuống cho tiêu đề
        )

        // Thêm một Spacer để tạo khoảng cách giữa tiêu đề và các phần tử bên dưới
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp), // Điều chỉnh khoảng cách với tiêu đề
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(workoutItems) { item ->
                WorkoutCard(item = item, navController = navController)
            }
        }
    }
}

@Composable
fun WorkoutCard(item: WorkoutItem, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                navController.navigate(item.route) // Điều hướng đến màn hình chi tiết của bài tập
            },
        elevation = CardDefaults.cardElevation(12.dp), // Bóng đổ mạnh hơn để thẻ nổi bật
        shape = RoundedCornerShape(16.dp)  // Các góc mềm mại hơn cho thẻ
    ) {
        Box {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))  // Tăng độ tối của overlay để làm nổi bật chữ
            )
            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold, // Chữ đậm cho tiêu đề bài tập
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

data class WorkoutItem(val title: String, val imageResId: Int, val route: String)
