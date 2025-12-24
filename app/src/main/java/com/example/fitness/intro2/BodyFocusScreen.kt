package com.example.fitness.intro2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitness.style.*
@Composable
fun BodyFocusScreen(navController: NavHostController) {
    var selectedPart by remember { mutableStateOf("") }
    val parts = listOf("Toàn thân", "Cánh tay", "Ngực", "Bụng", "Chân")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        ScreenHeader(title = "Hãy chọn vùng tập trung\ncủa bạn")

        Row(modifier = Modifier.weight(1f)) {
            // Cột TRÁI: Danh sách nút chọn
            Column(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                parts.forEach { part ->
                    val isSelected = selectedPart == part
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(50.dp)
                            .background(
                                color = if (isSelected) BrandBlue else Color.White,
                                shape = RoundedCornerShape(25.dp)
                            )
                            // Tạo viền xám nếu không chọn
                            .then(if(!isSelected) Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(25.dp)) else Modifier)
                            .clickable { selectedPart = part },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = part.uppercase(),
                            color = if (isSelected) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Cột PHẢI: Chỗ để ảnh người (Placeholder)
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxHeight()
//                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                //Text("Ảnh Body", color = Color.Gray)
//                // Sau này bạn thay bằng: Image(painter = painterResource(R.drawable.body_img)...)
//            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bấm Next -> Chuyển sang màn Cân nặng/Chiều cao
        NextButton(onClick = { navController.navigate("muctieu") })
    }
}