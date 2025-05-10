package com.example.fitness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AgeDetailsScreen(navController: NavController, ageRange: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Thông tin chi tiết cho độ tuổi: $ageRange",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Đây là nơi bạn sẽ hiển thị thông tin chi tiết về độ tuổi $ageRange. \nVí dụ: các bài tập phù hợp, chế độ dinh dưỡng, lời khuyên, v.v.",
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Quay lại")
        }
    }
}

@Composable
fun WorkoutDetailsScreen(navController: NavController, workoutName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Thông tin chi tiết cho bài tập: $workoutName",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Đây là nơi bạn sẽ hiển thị thông tin chi tiết về bài tập $workoutName. \nVí dụ: các bước thực hiện, số lần lặp, video hướng dẫn, v.v.",
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Quay lại")
        }
    }
}
