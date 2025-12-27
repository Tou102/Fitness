package com.example.fitness.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LevelScreen(
    onLevelSelected: (DifficultyLevel) -> Unit // Sự kiện khi người dùng chọn level
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Màu nền đen ngầu
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CHỌN MỨC ĐỘ",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // Nút Level 1: EASY
        LevelButton(text = "MỨC 1 (DỄ)", color = Color.Green) {
            onLevelSelected(DifficultyLevel.EASY)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nút Level 2: MEDIUM
        LevelButton(text = "MỨC 2 (VỪA)", color = Color.Yellow) {
            onLevelSelected(DifficultyLevel.MEDIUM)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nút Level 3: HARD
        LevelButton(text = "MỨC 3 (KHÓ)", color = Color.Red) {
            onLevelSelected(DifficultyLevel.HARD)
        }
    }
}

@Composable
fun LevelButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}