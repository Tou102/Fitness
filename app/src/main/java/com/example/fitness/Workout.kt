package com.example.fitness

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitness.dao.WorkoutSessionDao
import com.example.fitness.db.AppDatabase
import com.example.fitness.entity.WorkoutSession
import com.example.fitness.viewModel.WorkoutViewModel
import kotlinx.coroutines.launch

// --- ViewModel Factory để tạo WorkoutViewModel ---

class WorkoutViewModelFactory(
    private val workoutDao: WorkoutSessionDao
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
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
    userId: Int,
    isAdmin: Boolean
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val workoutViewModel: WorkoutViewModel = viewModel(
        factory = WorkoutViewModelFactory(db.workoutSessionDao())
    )

    WorkoutScreen(
        navController = navController,
        workoutViewModel = workoutViewModel,
        userId = userId,
        isAdmin = isAdmin
    )
}

// --- WorkoutScreen chính hiển thị toàn bộ danh sách bài tập ---

@Composable
fun WorkoutScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel,
    userId: Int,
    isAdmin: Boolean
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        workoutViewModel.loadWeeklySessions(userId)
    }

    val weeklySessions by workoutViewModel.weeklySessions.collectAsState(initial = emptyList())

    val workoutItems = listOf(
        WorkoutItem("FULLBODY", R.drawable.fullbody, "workoutDetails/FULLBODY"),
        WorkoutItem("ABS", R.drawable.abs, "workoutDetails/ABS"),
        WorkoutItem("CHEST", R.drawable.chest, "workoutDetails/CHEST"),
        WorkoutItem("ARM", R.drawable.arm, "workoutDetails/ARM"),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
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
            isAdmin = isAdmin,
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
    weeklySessions: List<WorkoutSession>,
    isAdmin: Boolean,
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
                    isAdmin = isAdmin,
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
    isAdmin: Boolean,
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
            .height(220.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = {
                    isPressed = true
                    navController.navigate(item.route + "?isAdmin=$isAdmin")
                },
                onClickLabel = "Select ${item.title}"
            )
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            androidx.compose.foundation.Image(
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

// --- Các màn hình chi tiết bài tập ---

@Composable
fun FullBody(
    navController: NavHostController,
    exerciseViewModel: com.example.fitness.viewModel.ExerciseViewModel,
    isAdmin: Boolean
) {
    ExerciseGroupScreen(
        navController = navController,
        exerciseViewModel = exerciseViewModel,
        groupName = "FullBody",
        title = "Bài tập full body",
        description = "Bài tập toàn thân phù hợp cho mọi cấp độ, giúp tăng sức mạnh và sự dẻo dai.",
        benefits = listOf(
            "Tăng cường sức mạnh cơ bắp toàn diện",
            "Cải thiện sức bền và khả năng vận động",
            "Đốt cháy calo hiệu quả",
            "Hỗ trợ giảm cân và duy trì vóc dáng",
            "Tăng cường sức khỏe tim mạch",
            "Phù hợp với mọi trình độ tập luyện"
        ),
        isAdmin = isAdmin
    )
}

@Composable
fun Abs(
    navController: NavHostController,
    exerciseViewModel: com.example.fitness.viewModel.ExerciseViewModel,
    isAdmin: Boolean
) {
    ExerciseGroupScreen(
        navController = navController,
        exerciseViewModel = exerciseViewModel,
        groupName = "Abs",
        title = "Bài tập cơ bụng (Abs)",
        description = "Bài tập giúp tăng cường sức mạnh cơ bụng, hỗ trợ giữ thăng bằng và cải thiện vóc dáng.",
        benefits = listOf(
            "Tăng sức mạnh vùng bụng",
            "Cải thiện tư thế và thăng bằng",
            "Hỗ trợ giảm mỡ vùng bụng",
            "Giúp cơ thể săn chắc hơn"
        ),
        isAdmin = isAdmin
    )
}

@Composable
fun Chest(
    navController: NavHostController,
    exerciseViewModel: com.example.fitness.viewModel.ExerciseViewModel,
    isAdmin: Boolean
) {
    ExerciseGroupScreen(
        navController = navController,
        exerciseViewModel = exerciseViewModel,
        groupName = "Chest",
        title = "Bài tập ngực (Chest)",
        description = "Các bài tập tăng cường sức mạnh cơ ngực, giúp phát triển cơ bắp và cải thiện sức mạnh tổng thể.",
        benefits = listOf(
            "Tăng cường sức mạnh cơ ngực",
            "Cải thiện tư thế và sức khỏe tổng thể",
            "Phát triển cơ bắp săn chắc",
            "Hỗ trợ thực hiện các hoạt động thể chất khác"
        ),
        isAdmin = isAdmin
    )
}

@Composable
fun Arm(
    navController: NavHostController,
    exerciseViewModel: com.example.fitness.viewModel.ExerciseViewModel,
    isAdmin: Boolean
) {
    ExerciseGroupScreen(
        navController = navController,
        exerciseViewModel = exerciseViewModel,
        groupName = "Arm",
        title = "Bài tập tay (Arm)",
        description = "Các bài tập giúp phát triển cơ tay, tăng sức mạnh và sự săn chắc.",
        benefits = listOf(
            "Tăng sức mạnh cơ tay",
            "Cải thiện sức bền và độ săn chắc",
            "Hỗ trợ vận động và nâng đỡ",
            "Giúp phát triển vóc dáng cân đối"
        ),
        isAdmin = isAdmin
    )
}
