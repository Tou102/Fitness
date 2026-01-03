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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Palette màu xanh dương fitness hiện đại
val BluePrimary = Color(0xFF0D47A1)    // Xanh dương đậm
val BlueLight = Color(0xFF42A5F5)      // Xanh dương sáng (highlight)
val BlueGradientStart = Color(0xFF1976D2)
val BlueGradientEnd = Color(0xFF03DAC6) // Teal nhẹ cho gradient năng lượng
val BackgroundDark = Color(0xFF0A192F) // Nền tối sâu
val TextWhite = Color.White
val TextGray = Color(0xFFB0BEC5)

@Composable
fun LevelScreen(
    onLevelSelected: (DifficultyLevel) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundDark, Color.Black)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tiêu đề lớn, mạnh mẽ
            Text(
                text = "CHỌN\nMỨC ĐỘ TẬP",
                color = TextWhite,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                modifier = Modifier.padding(bottom = 60.dp)
            )

            // Level 1 - DỄ
            FitnessLevelButton(
                text = "MỨC 1",
                subText = "DỄ - PHÙ HỢP NGƯỜI MỚI",
                gradientStart = Color(0xFF4CAF50), // Xanh lá nhạt
                gradientEnd = Color(0xFF8BC34A),
                onClick = { onLevelSelected(DifficultyLevel.EASY) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Level 2 - VỪA
            FitnessLevelButton(
                text = "MỨC 2",
                subText = "VỪA - THỬ THÁCH HƠN",
                gradientStart = BlueGradientStart,
                gradientEnd = BlueLight,
                onClick = { onLevelSelected(DifficultyLevel.MEDIUM) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Level 3 - KHÓ
            FitnessLevelButton(
                text = "MỨC 3",
                subText = "KHÓ - DÀNH CHO PRO",
                gradientStart = Color(0xFFEF5350), // Đỏ cam
                gradientEnd = Color(0xFFFF7043),
                onClick = { onLevelSelected(DifficultyLevel.HARD) }
            )
        }
    }
}

@Composable
fun FitnessLevelButton(
    text: String,
    subText: String,
    gradientStart: Color,
    gradientEnd: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(gradientStart, gradientEnd)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    color = TextWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = subText,
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}