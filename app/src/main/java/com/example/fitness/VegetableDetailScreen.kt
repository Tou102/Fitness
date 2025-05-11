package com.example.fitness

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.Button // Import Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment

@Composable
fun VegetableDetailScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween, // đẩy button xuống dưới
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column { // giữ tiêu đề ở trên
            Text("Rau củ", style = MaterialTheme.typography.headlineMedium)
            // Thêm nội dung chi tiết tại đây...
        }

        Row( // thêm row để chứa button
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start // căn button về bên trái
        ) {
            Button(onClick = {
                navController.popBackStack() //quay về trang trước
            }) {
                Text("Quay về")
            }
        }
    }
}
