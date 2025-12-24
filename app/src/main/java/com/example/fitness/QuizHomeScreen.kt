package com.example.fitness

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// import enum dùng để lấy câu hỏi
// sửa lại cho đúng với file QuizQuestionsData.kt của ông
import com.example.fitness.WorkoutType

@Composable
fun QuizHomeScreen(
    navController: NavHostController
) {
    // ----- STATE -----
    var selectedLevel by remember { mutableStateOf<Int?>(null) }
    var selectedWorkout by remember { mutableStateOf<WorkoutType?>(null) }
    var showError by remember { mutableStateOf(false) }

    // map: level code -> label
    val levelOptions = listOf(
        1 to "Cơ bản",
        2 to "Nâng cao",
        3 to "Thực chiến"
    )

    // map: workout enum -> label hiển thị
    val workoutOptions = listOf(
        WorkoutType.FULLBODY to "Toàn thân",
        WorkoutType.CHEST    to "Ngực",
        WorkoutType.ABS     to "Bụng",   // nếu enum của ông khác thì đổi lại
        WorkoutType.ARM     to "Tay"     // ví dụ: ARM / ABS / v.v.
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5EAFE)) // nền nhạt giống app
    ) {
        // ===== HEADER =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000066),
                            Color(0xFF004D73)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // nút quay lại Menu MiniGame
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White
                )
            }

            // tiêu đề
            Text(
                text = "ĐỐ VUI",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ===== NỘI DUNG =====
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Text(
                text = "1. Chọn độ khó",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // các mức độ – màu nhẹ, rõ selected
            levelOptions.forEach { (value, label) ->
                LevelItem(
                    text = label,
                    selected = selectedLevel == value,
                    onClick = {
                        selectedLevel = value
                        showError = false
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "2. Chọn bài tập muốn ôn lại",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // các bài tập – màu khác để phân khối
            workoutOptions.forEach { (type, label) ->
                WorkoutItem(
                    text = label,
                    selected = selectedWorkout == type,
                    onClick = {
                        selectedWorkout = type
                        showError = false
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showError) {
                Text(
                    text = "Hãy chọn ĐỘ KHÓ và BÀI TẬP trước khi bắt đầu kiểm tra.",
                    color = Color(0xFFD32F2F),
                    style = MaterialTheme.typography.body2
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // nút bắt đầu
            val canStart = selectedLevel != null && selectedWorkout != null

            Button(
                onClick = {
                    if (!canStart) {
                        showError = true
                    } else {
                        val workoutName = selectedWorkout!!.name   // VD: FULLBODY
                        val level = selectedLevel!!                // 1, 2, 3

                        navController.navigate("quiz_play/$workoutName/$level")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = canStart,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (canStart)
                        Color(0xFF0288D1)
                    else
                        Color(0xFFB0BEC5),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Bắt đầu kiểm tra",
                    style = MaterialTheme.typography.button.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/* -------- ITEM ĐỘ KHÓ -------- */

@Composable
private fun LevelItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) Color(0xFFBBDEFB) else Color(0xFFE3F2FD)
    val border = if (selected) Color(0xFF1976D2) else Color(0xFFB0BEC5)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() },
        elevation = if (selected) 6.dp else 2.dp,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = bg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(color = bg),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(26.dp)
                        .background(border, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = Color(0xFF0D1B2A)
                    )
                )
            }
        }
    }
}

/* -------- ITEM BÀI TẬP -------- */

@Composable
private fun WorkoutItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) Color(0xFFFFE0B2) else Color(0xFFFFF3E0)
    val border = if (selected) Color(0xFFF57C00) else Color(0xFFFFCC80)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        elevation = if (selected) 6.dp else 2.dp,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = bg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(bg),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(border, RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = Color(0xFF3E2723)
                    )
                )
            }
        }
    }
}
