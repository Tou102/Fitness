package com.example.fitness

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun WorkoutScreen(navController: NavHostController) {

    val workoutItems = listOf(
        WorkoutItem("FULL BODY", R.drawable.nho, "workoutDetails/Fullbody"),
        WorkoutItem("ABS", R.drawable.nguoi_lon, "workoutDetails/Abs"),
        WorkoutItem("CHEST", R.drawable.download, "workoutDetails/Chest"),
        WorkoutItem("ARM", R.drawable.tapvo, "workoutDetails/Arm"),


    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFE3F2FD))
    ) {
        Text(
            text = "Bài tập",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(workoutItems) { item ->
                WorkoutCard(item = item, navController = navController)
            }
        }

        Row( // Thêm Row để chứa hai nút
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Để các nút ở hai đầu
        ) {
            Button(
                onClick = { navController.navigate("nutrition") }, // Điều hướng về màn hình chế độ dinh dưỡng
                modifier = Modifier.weight(1f) // Chia đều không gian
            ) {

                Text("Quay lại")
            }

            Spacer(modifier = Modifier.width(16.dp)) // Thêm khoảng cách giữa hai nút

            Button(
                onClick = { navController.navigate("home") }, // Điều hướng đến màn hình chính
                modifier = Modifier.weight(1f) // Chia đều không gian
            ) {
                Text("Tiếp tục")
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
        elevation = CardDefaults.cardElevation(6.dp)
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
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}

data class WorkoutItem(val title: String, val imageResId: Int, val route: String)

