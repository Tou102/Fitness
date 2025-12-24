package com.example.fitness

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.fitness.entity.AppRepository
import com.example.fitness.entity.Exercisee
import com.example.fitness.entity.MeasureUnit

@Composable
fun PlanDetailScreen(
    planId: Int,
    onBack: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    onStartWorkout: () -> Unit
) {
    val exercises = remember(planId) { AppRepository.getExercisesForPlan(planId) }
    val context = LocalContext.current

    // 1. CẤU HÌNH BỘ ĐỌC GIF
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
            ) {
                Text("KHỞI ĐẦU", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Danh sách bài tập", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            // List Bài tập
            LazyColumn {
                items(exercises) { exercise ->
                    ExerciseRowItem(
                        exercise = exercise,
                        imageLoader = imageLoader, // Truyền bộ đọc GIF xuống
                        onClick = { onExerciseClick(exercise.id) }
                    )
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun ExerciseRowItem(
    exercise: Exercisee,
    imageLoader: ImageLoader, // Nhận bộ đọc GIF
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- ẢNH GIF TRONG DANH SÁCH ---
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(exercise.imageRes ) // <--- LẤY GIF RA CHẠY
                    .size(Size.ORIGINAL)   // Giữ size gốc (hoặc dùng .size(200) để nén bớt)
                    .build(),
                imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp) // Tăng kích thước lên chút cho dễ nhìn GIF
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEEEEE)), // Nền xám nhẹ nếu GIF load chậm
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = exercise.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            val unitText = if (exercise.unit == MeasureUnit.TIME) "${exercise.value}s" else "x${exercise.value}"
            Text(text = unitText, color = Color.Gray)
        }

        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray)
    }
}