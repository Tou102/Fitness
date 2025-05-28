package com.example.fitness

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.fitness.dao.WorkoutSessionDao
import com.example.fitness.db.AppDatabase
import com.example.fitness.viewModel.WorkoutViewModel
import kotlinx.coroutines.launch

// --- ViewModel Factory để tạo WorkoutViewModel ---

class WorkoutViewModelFactory(
    private val workoutDao: WorkoutSessionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(workoutDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// --- WorkoutScreenRoute: Khởi tạo ViewModel và gọi WorkoutScreen ---

@Composable
fun WorkoutScreenRoute(
    navController: NavHostController,
    userId: Int
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val workoutViewModel: WorkoutViewModel = viewModel(
        factory = WorkoutViewModelFactory(db.workoutSessionDao())
    )

    WorkoutScreen(
        navController = navController,
        workoutViewModel = workoutViewModel,
        userId = userId
    )
}

// --- WorkoutScreen chính hiển thị toàn bộ danh sách bài tập ---

@Composable
fun WorkoutScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel,
    userId: Int
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Load lịch tập khi mở màn hình
    LaunchedEffect(userId) {
        workoutViewModel.loadWeeklySessions(userId)
    }

    val weeklySessions by workoutViewModel.weeklySessions.collectAsState(initial = emptyList())

    val workoutItems = listOf(
        WorkoutItem("FULL BODY", R.drawable.fullbody, "workoutDetails/Fullbody"),
        WorkoutItem("ABS", R.drawable.abs, "workoutDetails/Abs"),
        WorkoutItem("CHEST", R.drawable.chest, "workoutDetails/Chest"),
        WorkoutItem("ARM", R.drawable.arm, "workoutDetails/Arm"),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFF42A5F5))
                )
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        WorkoutListContent(
            navController = navController,
            workoutItems = workoutItems,
            workoutViewModel = workoutViewModel,
            userId = userId,
            weeklySessions = weeklySessions,
            onShowSnackbar = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// --- Nội dung danh sách bài tập ---

@Composable
fun WorkoutListContent(
    navController: NavHostController,
    workoutItems: List<WorkoutItem>,
    workoutViewModel: WorkoutViewModel,
    userId: Int,
    weeklySessions: List<com.example.fitness.entity.WorkoutSession>,
    onShowSnackbar: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tất cả bài tập tuần",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(workoutItems) { item ->
                val calendar = java.util.Calendar.getInstance()
                val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                val currentDay = if (dayOfWeek == java.util.Calendar.SUNDAY) 7 else dayOfWeek - 1

                val isCheckedIn = weeklySessions.any {
                    it.userId == userId && it.day == currentDay && it.workoutType == item.title && it.completed
                }
                WorkoutCard(
                    item = item,
                    navController = navController,
                    isVisible = true,
                    isCheckedIn = isCheckedIn,
                    onCheckIn = {
                        workoutViewModel.checkInWorkout(userId, currentDay, item.title)
                        onShowSnackbar("Đã check-in bài tập ${item.title}!")
                    }
                )
            }

        }

    }
}

// --- Card bài tập ---

@Composable
fun WorkoutCard(
    item: WorkoutItem,
    navController: NavHostController,
    isVisible: Boolean,
    isCheckedIn: Boolean,
    onCheckIn: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp) // tăng chiều cao để đủ chỗ cho nút
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = {
                    isPressed = true
                    navController.navigate(item.route)
                },
                onClickLabel = "Select ${item.title}"
            )
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )

            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                        blurRadius = 2f
                    )
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )

            Button(
                onClick = { if (!isCheckedIn) onCheckIn() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .height(36.dp),
                colors = if (isCheckedIn) {
                    ButtonDefaults.buttonColors(containerColor = Color.Gray)
                } else {
                    ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                },
                enabled = !isCheckedIn
            ) {
                Text(
                    text = if (isCheckedIn) "Đã tập" else "Check-in",
                    color = Color.White
                )
            }
        }
    }
}

// --- Data class bài tập ---

data class WorkoutItem(val title: String, val imageResId: Int, val route: String)
