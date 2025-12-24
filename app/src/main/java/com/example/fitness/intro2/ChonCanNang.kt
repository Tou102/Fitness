package com.example.fitness.intro2

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitness.style.*
@Composable
fun ChonCanNang(navController: NavHostController) {
    var weight by remember { mutableFloatStateOf(75f) }
    var height by remember { mutableFloatStateOf(175f) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeader(title = "Hãy cho chúng tôi biết\nthêm về bạn", subtitle = "Để giúp tăng kết quả tập luyện")

        Spacer(modifier = Modifier.height(30.dp))

        // --- Phần Cân Nặng ---
        Text(text = "Cân nặng", fontSize = 16.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = String.format("%.0f", weight), fontSize = 56.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
            Text(text = "kg", fontSize = 20.sp, modifier = Modifier.padding(bottom = 12.dp, start = 4.dp), fontWeight = FontWeight.Bold)
        }
        Slider(
            value = weight,
            onValueChange = { weight = it },
            valueRange = 40f..150f,
            colors = SliderDefaults.colors(thumbColor = BrandBlue, activeTrackColor = BrandBlue)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- Phần Chiều Cao ---
        Text(text = "Chiều cao", fontSize = 16.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = String.format("%.0f", height), fontSize = 56.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
            Text(text = "cm", fontSize = 20.sp, modifier = Modifier.padding(bottom = 12.dp, start = 4.dp), fontWeight = FontWeight.Bold)
        }
        Slider(
            value = height,
            onValueChange = { height = it },
            valueRange = 140f..220f,
            colors = SliderDefaults.colors(thumbColor = BrandBlue, activeTrackColor = BrandBlue)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bấm Next -> Kết thúc Intro (Ví dụ vào màn Home)
        NextButton(
            text = "BẮT ĐẦU KẾ HOẠCH",
            onClick = { navController.navigate("workout") }
        )
    }
}