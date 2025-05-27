package com.example.fitness

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseCameraScreen(exerciseId: Int) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Đếm rep cho bài tập ID: $exerciseId",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(16.dp)
//        )
        // TODO: tích hợp CameraX + ML Kit vào đây
        CameraScreen() // phần này bạn đã có rồi
    }
}
