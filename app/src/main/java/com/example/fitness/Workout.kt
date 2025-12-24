//package com.example.fitness
//
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Shadow
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import com.example.fitness.dao.WorkoutSessionDao
//import com.example.fitness.db.AppDatabase
//import com.example.fitness.entity.WorkoutSession
//import com.example.fitness.viewModel.ExerciseViewModel
//import com.example.fitness.viewModel.WorkoutViewModel
//import kotlinx.coroutines.launch
//import java.util.Calendar
//
//// ======================== LỊCH TẬP CỐ ĐỊNH 7 NGÀY ========================
//private val weeklySchedule = mapOf(
//    1 to "FULLBODY",   // Thứ 2
//    2 to "ABS",        // Thứ 3
//    3 to "CHEST",      // Thứ 4
//    4 to "ARM",        // Thứ 5
//    5 to "FULLBODY",   // Thứ 6
//    6 to "ABS",        // Thứ 7
//    7 to "CHEST"       // Chủ nhật
//)
//
//// ======================== ViewModel Factory ========================
//class WorkoutViewModelFactory(
//    private val workoutDao: WorkoutSessionDao
//) : androidx.lifecycle.ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
//            return WorkoutViewModel(workoutDao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
//
//// ======================== Route ========================
//@Composable
//fun WorkoutScreenRoute(
//    navController: NavHostController,
//    userId: Int,
//    isAdmin: Boolean
//) {
//    val context = LocalContext.current
//    val db = AppDatabase.getDatabase(context)
//    val workoutViewModel: WorkoutViewModel = viewModel(
//        factory = WorkoutViewModelFactory(db.workoutSessionDao())
//    )
//
//    WorkoutScreen(
//        navController = navController,
//        workoutViewModel = workoutViewModel,
//        userId = userId,
//        isAdmin = isAdmin
//    )
//}
//
//// ======================== WorkoutScreen chính ========================
//@Composable
//fun WorkoutScreen(
//    navController: NavHostController,
//    workoutViewModel: WorkoutViewModel,
//    userId: Int,
//    isAdmin: Boolean
//) {
//    val snackbarHostState = remember { SnackbarHostState() }
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(userId) {
//        workoutViewModel.loadWeeklySessions(userId)
//    }
//
//    val weeklySessions by workoutViewModel.weeklySessions.collectAsState(initial = emptyList())
//
//    val workoutItems = listOf(
//        WorkoutItem("FULLBODY", R.drawable.fullbody, "workoutDetails/FULLBODY"),
//        WorkoutItem("ABS", R.drawable.abs, "workoutDetails/ABS"),
//        WorkoutItem("CHEST", R.drawable.chest, "workoutDetails/CHEST"),
//        WorkoutItem("ARM", R.drawable.arm, "workoutDetails/ARM")
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(Color(0xFF2196F3), Color(0xFF42A5F5))
//                )
//            )
//            .padding(horizontal = 16.dp, vertical = 24.dp)
//    ) {
//        WorkoutListContent(
//            navController = navController,
//            workoutItems = workoutItems,
//            workoutViewModel = workoutViewModel,
//            userId = userId,
//            weeklySessions = weeklySessions,
//            isAdmin = isAdmin,
//            onShowSnackbar = { message ->
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar(message)
//                }
//            }
//        )
//        SnackbarHost(
//            hostState = snackbarHostState,
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )
//    }
//}
//
//// ======================== Danh sách bài tập ========================
//@Composable
//fun WorkoutListContent(
//    navController: NavHostController,
//    workoutItems: List<WorkoutItem>,
//    workoutViewModel: WorkoutViewModel,
//    userId: Int,
//    weeklySessions: List<WorkoutSession>,
//    isAdmin: Boolean,
//    onShowSnackbar: (String) -> Unit
//) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Lịch tập trong tuần",
//            style = MaterialTheme.typography.headlineLarge.copy(
//                color = Color.White,
//                fontWeight = FontWeight.ExtraBold,
//                fontSize = 36.sp,
//                shadow = Shadow(
//                    color = Color.Black.copy(alpha = 0.4f),
//                    offset = androidx.compose.ui.geometry.Offset(3f, 3f),
//                    blurRadius = 6f
//                )
//            ),
//            modifier = Modifier.padding(vertical = 32.dp)
//        )
//
//        val calendar = Calendar.getInstance()
//        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
//        val today = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
//
//        val todayName = when (today) {
//            1 -> "Thứ Hai"
//            2 -> "Thứ Ba"
//            3 -> "Thứ Tư"
//            4 -> "Thứ Năm"
//            5 -> "Thứ Sáu"
//            6 -> "Thứ Bảy"
//            7 -> "Chủ Nhật"
//            else -> "Hôm nay"
//        }
//
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(18.dp),
//            contentPadding = PaddingValues(bottom = 100.dp)
//        ) {
//            items(workoutItems) { item ->
//                val allowedDays = weeklySchedule.filterValues { it == item.title }.keys
//                val isTodayAllowed = today in allowedDays
//
//                val isCheckedIn = weeklySessions.any {
//                    it.userId == userId && it.day == today && it.workoutType == item.title && it.completed
//                }
//
//                WorkoutCard(
//                    item = item,
//                    navController = navController,
//                    isTodayAllowed = isTodayAllowed,
//                    isCheckedIn = isCheckedIn,
//                    todayName = todayName,
//                    isAdmin = isAdmin,
//                    onCheckIn = {
//                        workoutViewModel.checkInWorkout(userId, today, item.title)
//                        onShowSnackbar("Check-in ${item.title} thành công!")
//                    },
//                    onLockedClick = {
//                        onShowSnackbar("Hôm nay $todayName chưa phải ngày tập ${item.title} đâu nhé!")
//                    }
//                )
//            }
//        }
//    }
//}
//
//// ======================== Card bài tập ========================
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun WorkoutCard(
//    item: WorkoutItem,
//    navController: NavHostController,
//    isTodayAllowed: Boolean,
//    isCheckedIn: Boolean,
//    todayName: String,
//    isAdmin: Boolean,
//    onCheckIn: () -> Unit,
//    onLockedClick: () -> Unit
//) {
//    var isPressed by remember { mutableStateOf(false) }
//    val scale by animateFloatAsState(
//        targetValue = if (isPressed) 0.95f else 1f,
//        animationSpec = tween(150)
//    )
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(230.dp)
//            .scale(scale)
//            .shadow(12.dp, RoundedCornerShape(20.dp))
//            .clip(RoundedCornerShape(20.dp))
//            .clickable(
//                enabled = isTodayAllowed || isAdmin,
//                onClick = {
//                    isPressed = true
//                    if (isTodayAllowed || isAdmin) {
//                        navController.navigate("${item.route}?isAdmin=$isAdmin")
//                    } else {
//                        onLockedClick()
//                    }
//                }
//            )
//            .alpha(if (isTodayAllowed || isAdmin) 1f else 0.5f),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = if (isTodayAllowed) 10.dp else 3.dp)
//    ) {
//            Box {
//            Image(
//                painter = painterResource(id = item.imageResId),
//                contentDescription = item.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
//
//            if (!isTodayAllowed && !isAdmin) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.Black.copy(alpha = 0.65f)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Icon(
//                            imageVector = Icons.Default.Lock,
//                            contentDescription = "Khóa",
//                            tint = Color.White,
//                            modifier = Modifier.size(80.dp)
//                        )
//                        Spacer(modifier = Modifier.height(12.dp))
//                        Text(
//                            text = "Chưa tới ngày tập",
//                            color = Color.White,
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.SemiBold
//                        )
//                    }
//                }
//            }
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        Brush.verticalGradient(
//                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
//                        )
//                    )
//            )
//
//            Column(
//                modifier = Modifier
//                    .align(Alignment.BottomStart)
//                    .padding(20.dp)
//            ) {
//                Text(
//                    text = item.title,
//                    color = Color.White,
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.ExtraBold
//                )
//                if (isTodayAllowed || isAdmin) {
//                    Text(
//                        text = if (isTodayAllowed) "Hôm nay được tập" else "Admin mode",
//                        color = Color.White.copy(alpha = 0.9f),
//                        fontSize = 15.sp
//                    )
//                }
//            }
//
//            Button(
//                onClick = { if (isTodayAllowed && !isCheckedIn) onCheckIn() },
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(20.dp)
//                    .height(42.dp),
//                enabled = isTodayAllowed && !isCheckedIn,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (isCheckedIn) Color(0xFF607D8B) else Color(0xFF4CAF50),
//                    disabledContainerColor = Color(0xFFB0BEC5)
//                )
//            ) {
//                Text(
//                    text = when {
//                        isCheckedIn -> "Đã tập"
//                        isTodayAllowed -> "Check-in"
//                        else -> "Chưa tới ngày"
//                    },
//                    color = Color.White,
//                    fontSize = 15.sp,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//        }
//    }
//}
//
//// ======================== Data class ========================
//data class WorkoutItem(
//    val title: String,
//    val imageResId: Int,
//    val route: String
//)
//
//// ======================== Các màn hình chi tiết ========================
//@Composable
//fun FullBody(
//    navController: NavHostController,
//    exerciseViewModel: ExerciseViewModel,
//    isAdmin: Boolean = false
//) {
//    ExerciseGroupScreen(
//        navController = navController,
//        exerciseViewModel = exerciseViewModel,
//        groupName = "FullBody",
//        title = "Bài tập full body",
//        description = "Bài tập toàn thân phù hợp cho mọi cấp độ, giúp tăng sức mạnh và sự dẻo dai.",
//        benefits = listOf(
//            "Tăng cường sức mạnh cơ bắp toàn diện",
//            "Cải thiện sức bền và khả năng vận động",
//            "Đốt cháy calo hiệu quả",
//            "Hỗ trợ giảm cân và duy trì vóc dáng",
//            "Tăng cường sức khỏe tim mạch",
//            "Phù hợp với mọi trình độ tập luyện"
//        ),
//        isAdmin = isAdmin
//    )
//}
//
//@Composable
//fun Abs(
//    navController: NavHostController,
//    exerciseViewModel: ExerciseViewModel,
//    isAdmin: Boolean = false
//) {
//    ExerciseGroupScreen(
//        navController = navController,
//        exerciseViewModel = exerciseViewModel,
//        groupName = "Abs",
//        title = "Bài tập cơ bụng (Abs)",
//        description = "Bài tập giúp tăng cường sức mạnh cơ bụng, hỗ trợ giữ thăng bằng và cải thiện vóc dáng.",
//        benefits = listOf(
//            "Tăng sức mạnh vùng bụng",
//            "Cải thiện tư thế và thăng bằng",
//            "Hỗ trợ giảm mỡ vùng bụng",
//            "Giúp cơ thể săn chắc hơn"
//        ),
//        isAdmin = isAdmin
//    )
//}
//
//@Composable
//fun Chest(
//    navController: NavHostController,
//    exerciseViewModel: ExerciseViewModel,
//    isAdmin: Boolean = false
//) {
//    ExerciseGroupScreen(
//        navController = navController,
//        exerciseViewModel = exerciseViewModel,
//        groupName = "Chest",
//        title = "Bài tập ngực (Chest)",
//        description = "Các bài tập tăng cường sức mạnh cơ ngực, giúp phát triển cơ bắp và cải thiện sức mạnh tổng thể.",
//        benefits = listOf(
//            "Tăng cường sức mạnh cơ ngực",
//            "Cải thiện tư thế và sức khỏe tổng thể",
//            "Phát triển cơ bắp săn chắc",
//            "Hỗ trợ thực hiện các hoạt động thể chất khác"
//        ),
//        isAdmin = isAdmin
//    )
//}
//
//@Composable
//fun Arm(
//    navController: NavHostController,
//    exerciseViewModel: ExerciseViewModel,
//    isAdmin: Boolean = false
//) {
//    ExerciseGroupScreen(
//        navController = navController,
//        exerciseViewModel = exerciseViewModel,
//        groupName = "Arm",
//        title = "Bài tập tay (Arm)",
//        description = "Các bài tập giúp phát triển cơ tay, tăng sức mạnh và sự săn chắc.",
//        benefits = listOf(
//            "Tăng sức mạnh cơ tay",
//            "Cải thiện sức bền và độ săn chắc",
//            "Hỗ trợ vận động và nâng đỡ",
//            "Giúp phát triển vóc dáng cân đối"
//        ),
//        isAdmin = isAdmin
//    )
//}