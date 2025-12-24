package com.example.fitness.ui.screens // Hoặc package của bạn

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Menu

import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.runtime.*

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitness.R
import com.example.fitness.utils.copyUriToInternalStorage
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModel.WorkoutViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    workoutViewModel: WorkoutViewModel,
    userId: Int
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showLeaderboard by remember { mutableStateOf(false) }
    val user by userViewModel.user.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var username by rememberSaveable(user) { mutableStateOf("Người dùng") }
    var avatarUri by rememberSaveable(user) { mutableStateOf<Uri?>(null) }
    var expandedMenu by remember { mutableStateOf(false) }
    var infoSavedVisible by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        val currentUser = user
        if (currentUser != null) {
            username = currentUser.nickname ?: currentUser.username ?: "Người dùng"
            avatarUri = currentUser.avatarUriString?.let { Uri.parse(it) }
        } else {
            username = "Người dùng"
            avatarUri = null
        }
    }

    LaunchedEffect(userId) {
        workoutViewModel.loadWeeklySessions(userId)
    }

    val weeklySessions by workoutViewModel.weeklySessions.collectAsState(initial = emptyList())

    val weeklySchedule = remember(weeklySessions) {
        val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        days.mapIndexed { index, day ->
            day to weeklySessions.any { it.day == (index + 1) && it.completed }
        }.toMap()
    }

    if (infoSavedVisible) {
        InfoSavedScreen(onBack = { infoSavedVisible = false })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF4A90E2), Color(0xFF82B1FF))
                    )
                )
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFF2A6EDB), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val points = listOf(
                    Offset(width * 0.15f, height * 0.2f),
                    Offset(width * 0.5f, height * 0.1f),
                    Offset(width * 0.8f, height * 0.25f),
                    Offset(width * 0.3f, height * 0.5f),
                    Offset(width * 0.7f, height * 0.55f),
                    Offset(width * 0.85f, height * 0.4f)
                )
                points.forEach { point ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.15f),
                        radius = 12f,
                        center = point
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.TopStart)) {
                IconButton(onClick = { expandedMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Đăng xuất") },
                        onClick = {
                            expandedMenu = false
                            userViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Avatar + Hello
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { showEditDialog = true }
                    ) {
                        if (avatarUri != null) {
                            AsyncImage(
                                model = avatarUri,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Center)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_avatar_placeholder),
                                contentDescription = "Avatar mặc định",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Center)
                            )
                        }
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Chỉnh sửa avatar",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Text(
                        text = "Hello, $username",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    )
                }

                // 3 nút nhanh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { navController.navigate("bmi") },
                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A56DB)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Calculate, "Tính BMI", tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Tính BMI", color = Color.White, fontSize = 16.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(

                            onClick = { navController.navigate("water") },
                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A56DB)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.LocalDrink, "Nước", tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Nước", color = Color.White, fontSize = 16.sp)

                            onClick = { navController.navigate("Minigame") },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A56DB)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideogameAsset,
                                contentDescription = "Minigame",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mini Game",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )

                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { navController.navigate("calories_daily_summary") },
                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A56DB)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.FitnessCenter, "Calo", tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Calo", color = Color.White, fontSize = 16.sp)
                    }
                }

                // Lịch tuần
                WeeklyWorkoutSchedule(weeklySchedule = weeklySchedule)

                // === PHẦN MỚI: Streak + Stats Cards ===
                Spacer(modifier = Modifier.height(24.dp))

                // Streak Counter
                val completedDaysCount = weeklySchedule.count { it.value }
                val currentStreak = completedDaysCount // Có thể thay bằng logic streak thật

                val animatedStreak by animateFloatAsState(
                    targetValue = currentStreak.toFloat(),
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 200f)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A56DB)),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Chuỗi tập luyện 🔥", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Giữ lửa nào!", color = Color.White.copy(0.8f), fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = animatedStreak.toInt().toString(),
                                color = Color.White,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(" ngày", color = Color.White.copy(0.8f), fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
                        }

                        Icon(Icons.Default.Whatshot, "Streak", tint = Color(0xFFFF5722), modifier = Modifier.size(64.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Cards (cuộn ngang)
                val scrollState = rememberScrollState()

                Row(modifier = Modifier.horizontalScroll(scrollState)) {
                    StatsCard(
                        icon = Icons.Default.DirectionsWalk,
                        title = "Bước chân",
                        value = "8.542",      // Thay bằng data thật sau
                        target = "10.000",
                        progress = 0.75f
                    )
                    Spacer(Modifier.width(16.dp))
                    StatsCard(
                        icon = Icons.Default.Whatshot,
                        title = "Calo đốt",
                        value = "482",
                        target = "780",
                        progress = 0.62f
                    )
                    Spacer(Modifier.width(16.dp))
                    StatsCard(
                        icon = Icons.Default.LocalDrink,
                        title = "Nước uống",
                        value = "1.8L",
                        target = "2L",
                        progress = 0.9f
                    )
                    Spacer(Modifier.width(16.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))
                // Có thể thêm nút Bảng xếp hạng ở đây nếu muốn
            }
        }

        // Dialog chỉnh sửa profile (giữ nguyên)
        if (showEditDialog) {
            var tempUsername by remember(user) { mutableStateOf(username) }
            var tempAvatarUri by remember(user) { mutableStateOf(avatarUri) }
            val context = LocalContext.current

            val dialogLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    coroutineScope.launch {
                        val path = copyUriToInternalStorage(context, uri, "avatar_${System.currentTimeMillis()}.jpg")
                        if (path != null) tempAvatarUri = Uri.fromFile(File(path))
                    }
                }
            }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Chỉnh sửa hồ sơ") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempUsername,
                            onValueChange = { tempUsername = it },
                            label = { Text("Tên người dùng") },
                            singleLine = true
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Chọn ảnh đại diện:")
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { dialogLauncher.launch("image/*") }
                        ) {
                            if (tempAvatarUri != null) {
                                AsyncImage(model = tempAvatarUri, contentDescription = "Ảnh đại diện", modifier = Modifier.fillMaxSize())
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.ic_avatar_placeholder),
                                    contentDescription = "Ảnh đại diện mặc định",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        username = tempUsername
                        avatarUri = tempAvatarUri
                        showEditDialog = false
                        userViewModel.updateProfile(nickname = tempUsername, avatarUriString = tempAvatarUri?.toString())
                    }) { Text("Lưu") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) { Text("Hủy") }
                }
            )
        }
    }
}

// WeeklyWorkoutSchedule giữ nguyên
@Composable
fun WeeklyWorkoutSchedule(
    weeklySchedule: Map<String, Boolean>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90E2)),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Lịch tập luyện tuần này",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                days.forEach { day ->
                    val isWorkout = weeklySchedule[day] ?: false
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
                        Text(day, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = if (isWorkout) Color(0xFF2ECC71) else Color.White.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isWorkout) {
                                Icon(Icons.Default.FitnessCenter, "Đã tập", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// StatsCard mới
@Composable
fun StatsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    target: String,
    progress: Float
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFF1A56DB), modifier = Modifier.size(48.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Text("of $target", fontSize = 14.sp, color = Color.Gray)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(Color(0xFF4A90E2), RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun InfoSavedScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Thông tin đã được lưu!", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 24.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Quay lại")
        }
    }
}