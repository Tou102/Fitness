package com.example.fitness

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.fitness.entity.AppRepository
import com.example.fitness.entity.Exercisee
import com.example.fitness.entity.MeasureUnit
import kotlinx.coroutines.delay

// --- 1. ĐỊNH NGHĨA CÁC GIAI ĐOẠN ---
enum class WorkoutPhase {
    GET_READY,  // Đếm ngược 3-2-1
    EXERCISE,   // Đang tập
    REST,       // Nghỉ ngơi giữa hiệp
    COMPLETED   // Hoàn thành
}

// --- 2. MÀN HÌNH CHÍNH (QUẢN LÝ LUỒNG) ---
@Composable
fun WorkoutSessionScreen(
    planId: Int,
    onExit: () -> Unit
) {
    val context = LocalContext.current

    // 1. TẠO BỘ LOAD ẢNH GIF DÙNG CHUNG (TỐI ƯU BỘ NHỚ)
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            // Quan trọng: Tự động xóa bộ nhớ cache khi thiếu RAM
            .crossfade(true)
            .build()
    }

    // Lấy danh sách bài tập
    val exerciseList = remember { AppRepository.getExercisesForPlan(planId) }

    // State quản lý
    var currentIndex by remember { mutableIntStateOf(0) }
    var currentPhase by remember { mutableStateOf(WorkoutPhase.GET_READY) }

    if (exerciseList.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Gói tập này chưa có bài tập nào!")
            Button(onClick = onExit) { Text("Quay lại") }
        }
        return
    }

    // ĐIỀU HƯỚNG UI
    when (currentPhase) {
        WorkoutPhase.GET_READY -> {
            GetReadyView(
                onFinish = { currentPhase = WorkoutPhase.EXERCISE }
            )
        }
        WorkoutPhase.EXERCISE -> {
            ExerciseView(
                exercise = exerciseList[currentIndex],
                imageLoader = imageLoader, // Truyền bộ load xuống
                onDone = {
                    if (currentIndex >= exerciseList.size - 1) {
                        currentPhase = WorkoutPhase.COMPLETED
                    } else {
                        currentPhase = WorkoutPhase.REST
                    }
                }
            )
        }
        WorkoutPhase.REST -> {
            val nextExercise = exerciseList.getOrNull(currentIndex + 1)
            RestView(
                nextExercise = nextExercise,
                imageLoader = imageLoader, // Truyền bộ load xuống
                onSkip = {
                    currentIndex++
                    currentPhase = WorkoutPhase.EXERCISE
                }
            )
        }
        WorkoutPhase.COMPLETED -> {
            CompletedView(onExit = onExit)
        }
    }
}

// --- 3. UI: CHUẨN BỊ ---
@Composable
fun GetReadyView(onFinish: () -> Unit) {
    var timeLeft by remember { mutableIntStateOf(5) }

    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else {
            onFinish()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SẴN SÀNG", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2979FF))
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = if (timeLeft > 0) "$timeLeft" else "GO!",
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

// --- 4. UI: ĐANG TẬP (ĐÃ FIX LỖI MEMORY) ---
@Composable
fun ExerciseView(
    exercise: Exercisee,
    imageLoader: ImageLoader,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // A. ẢNH GIF (ĐÃ FIX: Bỏ Size.ORIGINAL)
        Box(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxWidth()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(exercise.imageRes)
                        .crossfade(true)
                        // .size(Size.ORIGINAL) <--- ĐÃ XÓA DÒNG NÀY ĐỂ TRÁNH CRASH
                        .build(),
                    imageLoader = imageLoader
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
        }

        // B. THÔNG TIN & ĐỒNG HỒ
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = exercise.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Vòng tròn đếm
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD))
            ) {
                if (exercise.unit == MeasureUnit.TIME) {
                    var timeLeft by remember { mutableIntStateOf(exercise.value) }
                    var isPaused by remember { mutableStateOf(false) }

                    LaunchedEffect(key1 = timeLeft, key2 = isPaused) {
                        if (timeLeft > 0 && !isPaused) {
                            delay(1000L)
                            timeLeft--
                        } else if (timeLeft == 0) {
                            onDone()
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$timeLeft", fontSize = 70.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2979FF))
                        Text("Giây", fontSize = 18.sp, color = Color.Gray)
                    }

                    IconButton(
                        onClick = { isPaused = !isPaused },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                    ) {
                        Icon(
                            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color(0xFF2979FF),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("x${exercise.value}", fontSize = 70.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2979FF))
                        Text("Lần", fontSize = 18.sp, color = Color.Gray)
                    }
                }
            }

            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("HOÀN THÀNH", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- 5. UI: NGHỈ NGƠI (ĐÃ FIX LỖI PREVIEW) ---
@Composable
fun RestView(
    nextExercise: Exercisee?,
    imageLoader: ImageLoader,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    var restTime by remember { mutableIntStateOf(15) }

    LaunchedEffect(key1 = restTime) {
        if (restTime > 0) {
            delay(1000L)
            restTime--
        } else {
            onSkip()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("NGHỈ NGƠI", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2979FF))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "00:${restTime.toString().padStart(2, '0')}",
            fontSize = 70.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (nextExercise != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TIẾP THEO:", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // FIX: Dùng Coil load thumbnail nhỏ (size 300) thay vì painterResource load full GIF
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(nextExercise.imageRes)
                                    .size(300) // Quan trọng: Giảm tải bộ nhớ
                                    .build(),
                                imageLoader = imageLoader
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(nextExercise.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (nextExercise.unit == MeasureUnit.TIME) "${nextExercise.value} giây" else "x${nextExercise.value} lần",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2979FF)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("BỎ QUA NGHỈ NGƠI", color = Color(0xFF2979FF), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.SkipNext, null, tint = Color(0xFF2979FF))
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

// --- 6. UI: HOÀN THÀNH ---
@Composable
fun CompletedView(onExit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏆", fontSize = 100.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text("CHÚC MỪNG!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2979FF))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Bạn đã hoàn thành bài tập xuất sắc.", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onExit,
            modifier = Modifier.width(220.dp).height(55.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
        ) {
            Text("VỀ TRANG CHỦ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}