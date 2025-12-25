package com.example.fitness

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.launch

// Phase giữ nguyên
enum class WorkoutPhase { GET_READY, EXERCISE, REST, COMPLETED }

// Màu sắc xanh dương đồng bộ toàn app
private val PrimaryBlue = Color(0xFF0EA5E9)     // Xanh dương neon chính
private val AccentBlue  = Color(0xFF0284C7)     // Xanh đậm highlight
private val GradientStart = Color(0xFFF0F9FF)   // Nền gradient đầu
private val GradientEnd   = Color(0xFFE0F2FE)   // Nền gradient cuối (xanh nhạt)

// Gradient background composable
@Composable
fun FitnessGradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        content()
    }
}

// Main Screen
@Composable
fun WorkoutSessionScreen(
    planId: Int,
    onExit: () -> Unit
) {
    val context = LocalContext.current

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }

    val exerciseList = remember { AppRepository.getExercisesForPlan(planId) }

    if (exerciseList.isEmpty()) {
        FitnessGradientBackground {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(
                    "Chưa có bài tập nào trong gói này!",
                    color = PrimaryBlue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = onExit, colors = ButtonDefaults.buttonColors(PrimaryBlue)) {
                    Text("Quay lại", color = Color.White)
                }
            }
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var currentPhase by remember { mutableStateOf(WorkoutPhase.GET_READY) }

    FitnessGradientBackground {
        when (currentPhase) {
            WorkoutPhase.GET_READY -> GetReadyView { currentPhase = WorkoutPhase.EXERCISE }
            WorkoutPhase.EXERCISE -> ExerciseView(
                exercise = exerciseList[currentIndex],
                imageLoader = imageLoader,
                onDone = {
                    if (currentIndex >= exerciseList.size - 1) {
                        currentPhase = WorkoutPhase.COMPLETED
                    } else {
                        currentPhase = WorkoutPhase.REST
                    }
                }
            )
            WorkoutPhase.REST -> RestView(
                nextExercise = exerciseList.getOrNull(currentIndex + 1),
                imageLoader = imageLoader,
                onSkip = {
                    currentIndex++
                    currentPhase = WorkoutPhase.EXERCISE
                }
            )
            WorkoutPhase.COMPLETED -> CompletedView(onExit)
        }
    }
}

// 1. Get Ready - Energetic countdown
@Composable
fun GetReadyView(onFinish: () -> Unit) {
    var timeLeft by remember { mutableIntStateOf(5) }
    val scale by animateFloatAsState(
        targetValue = if (timeLeft > 0) 1f else 1.3f,
        animationSpec = tween(600)
    )

    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else {
            delay(600L)
            onFinish()
        }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SẴN SÀNG NÀO!",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = AccentBlue,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(48.dp))

        Text(
            text = if (timeLeft > 0) "$timeLeft" else "GO!",
            fontSize = 160.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                brush = Brush.linearGradient(listOf(PrimaryBlue, AccentBlue))
            ),
            modifier = Modifier.scale(scale)
        )
    }
}

// 2. Exercise View - Modern timer + GIF full
@Composable
fun ExerciseView(
    exercise: Exercisee,
    imageLoader: ImageLoader,
    onDone: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {

        // GIF full màn hình trên
        Box(
            Modifier
                .weight(0.5f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .shadow(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(exercise.imageRes)
                        .crossfade(true)
                        .build(),
                    imageLoader = imageLoader
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
        }

        Column(
            Modifier
                .weight(0.5f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                exercise.name.uppercase(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryBlue,
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp
            )

            // Circular timer / reps
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
                if (exercise.unit == MeasureUnit.TIME) {
                    var timeLeft by remember { mutableIntStateOf(exercise.value) }
                    var isPaused by remember { mutableStateOf(false) }

                    val progress by animateFloatAsState(
                        targetValue = timeLeft.toFloat() / exercise.value.toFloat(),
                        animationSpec = tween(800)
                    )

                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(220.dp),
                        color = AccentBlue,
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeWidth = 12.dp
                    )

                    LaunchedEffect(timeLeft, isPaused) {
                        if (timeLeft > 0 && !isPaused) {
                            delay(1000L)
                            timeLeft--
                        } else if (timeLeft == 0) onDone()
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$timeLeft",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text("GIÂY", fontSize = 20.sp, color = AccentBlue)
                    }

                    IconButton(
                        onClick = { isPaused = !isPaused },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 80.dp)
                    ) {
                        Icon(
                            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            null,
                            tint = AccentBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else {
                    Text(
                        "x${exercise.value}",
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentBlue
                    )
                    Text("LẦN", fontSize = 22.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }

            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(PrimaryBlue, AccentBlue)))
                        .clip(RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("HOÀN THÀNH", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// 3. Rest View - Motivational card + gradient
@Composable
fun RestView(
    nextExercise: Exercisee?,
    imageLoader: ImageLoader,
    onSkip: () -> Unit
) {
    var restTime by remember { mutableIntStateOf(30) }

    LaunchedEffect(restTime) {
        if (restTime > 0) {
            delay(1000L)
            restTime--
        } else onSkip()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        Text(
            "NGHỈ NGƠI",
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            color = AccentBlue,
            letterSpacing = 3.sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "00:${restTime.toString().padStart(2, '0')}",
            fontSize = 100.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryBlue
        )

        Spacer(Modifier.height(48.dp))

        if (nextExercise != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.6f))
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "TIẾP THEO",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(nextExercise.imageRes)
                                    .size(400)
                                    .build(),
                                imageLoader = imageLoader
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(20.dp))
                        Column {
                            Text(
                                nextExercise.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                if (nextExercise.unit == MeasureUnit.TIME) "${nextExercise.value} giây" else "x${nextExercise.value} lần",
                                fontSize = 18.sp,
                                color = AccentBlue
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(2.dp, AccentBlue)
        ) {
            Text(
                "BỎ QUA NGHỈ",
                color = AccentBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Default.SkipNext, null, tint = AccentBlue, modifier = Modifier.size(32.dp))
        }
    }
}

// 4. Completed - Celebration vibe
@Composable
fun CompletedView(onExit: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showConfetti = true
        coroutineScope.launch { delay(3000L); showConfetti = false }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showConfetti) {
            Text("🎉🏆", fontSize = 120.sp) // Fallback confetti
        }

        Text(
            "HOÀN THÀNH XUẤT SẮC!",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = AccentBlue,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(32.dp))

        Icon(
            painter = painterResource(id = android.R.drawable.star_big_on), // Thay bằng trophy nếu có
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(140.dp)
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onExit,
            modifier = Modifier
                .width(280.dp)
                .height(70.dp),
            shape = RoundedCornerShape(35.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(PrimaryBlue, AccentBlue)))
                    .clip(RoundedCornerShape(35.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "VỀ TRANG CHỦ",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}