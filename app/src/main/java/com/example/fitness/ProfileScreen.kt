package com.example.fitness.ui.screens

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitness.R
import com.example.fitness.utils.copyUriToInternalStorage
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModel.WorkoutViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

// Màu sắc (giữ nguyên như bạn đã định nghĩa)
private val PrimaryBlue = Color(0xFF0EA5E9)
private val AccentBlue  = Color(0xFF0284C7)
private val SurfaceStart = Color(0xFFF0F9FF)
private val SurfaceEnd  = Color(0xFFE0F2FE)
private val CardBg      = Color.White.copy(alpha = 0.96f)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    workoutViewModel: WorkoutViewModel,
    userId: Int
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val user by userViewModel.user.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var username by rememberSaveable(user) { mutableStateOf("Người dùng") }
    var avatarUri by rememberSaveable(user) { mutableStateOf<Uri?>(null) }
    var expandedMenu by remember { mutableStateOf(false) }
    var infoSavedVisible by remember { mutableStateOf(false) }

    // Cập nhật thông tin khi user thay đổi
    LaunchedEffect(user) {
        user?.let {
            username = it.nickname ?: it.username ?: "Người dùng"
            avatarUri = it.avatarUriString?.let { uri -> Uri.parse(uri) }
        }
    }

    // Tự động reload dữ liệu khi màn hình được resume
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(userId, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            workoutViewModel.loadWeeklySessions(userId)
        }
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
            .background(Brush.verticalGradient(listOf(SurfaceStart, SurfaceEnd)))
            .padding(16.dp)
    ) {
        // Header với gradient xanh
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.75f))
                        )
                    )
                    .padding(24.dp)
            ) {
                IconButton(
                    onClick = { expandedMenu = true },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.Menu, null, tint = Color.White)
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

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                                    .align(Alignment.Center),
                                contentScale = ContentScale.Crop
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
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                                .background(Color.White.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, null, tint = Color.Black)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Hello, $username",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 3 nút hành động nhanh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Default.Calculate,
                label = "Tính BMI",
                onClick = { navController.navigate("bmi") },
                color = PrimaryBlue
            )

            ActionButton(
                icon = Icons.Default.VideogameAsset,
                label = "Mini Game",
                onClick = { navController.navigate("Minigame") },
                color = AccentBlue
            )

            ActionButton(
                icon = Icons.Default.FitnessCenter,
                label = "Calo",
                onClick = { navController.navigate("calories_daily_summary") },
                color = PrimaryBlue
            )
        }

        Spacer(Modifier.height(24.dp))

        // Lịch tập tuần
        WeeklyWorkoutSchedule(
            weeklySchedule = weeklySchedule,
            modifier = Modifier.fillMaxWidth(),
            onHistoryClick = { navController.navigate("history") }
        )

        Spacer(Modifier.height(24.dp))

        // Nút Bảng xếp hạng - đã kết nối đầy đủ
        Button(
            onClick = { navController.navigate("leaderboard") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Leaderboard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Bảng xếp hạng",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    // Dialog chỉnh sửa hồ sơ
    if (showEditDialog) {
        var tempUsername by remember(user) { mutableStateOf(username) }
        var tempAvatarUri by remember(user) { mutableStateOf(avatarUri) }
        val context = LocalContext.current

        val dialogLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                coroutineScope.launch {
                    val path = copyUriToInternalStorage(
                        context,
                        it,
                        "avatar_${System.currentTimeMillis()}.jpg"
                    )
                    if (path != null) {
                        tempAvatarUri = Uri.fromFile(File(path))
                    }
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = { tempUsername = it },
                        label = { Text("Tên người dùng") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Chọn ảnh đại diện:")
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { dialogLauncher.launch("image/*") }
                    ) {
                        if (tempAvatarUri != null) {
                            AsyncImage(
                                model = tempAvatarUri,
                                contentDescription = "Ảnh đại diện",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_avatar_placeholder),
                                contentDescription = "Ảnh đại diện mặc định",
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
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
                    userViewModel.updateProfile(
                        nickname = tempUsername,
                        avatarUriString = tempAvatarUri?.toString()
                    )
                }) {
                    Text("Lưu", color = PrimaryBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Hủy") }
            }
        )
    }
}

// Các composable phụ trợ (giữ nguyên từ code của bạn)
@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = color),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyWorkoutSchedule(
    weeklySchedule: Map<String, Boolean>,
    modifier: Modifier = Modifier,
    onHistoryClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable { onHistoryClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lịch tập luyện tuần này", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lịch sử", fontSize = 14.sp, color = TextSecondary)
                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                val todayIndex = LocalDate.now().dayOfWeek.value - 1  // Thứ 2 = 0, CN = 6

                days.forEachIndexed { index, day ->
                    val isWorkout = weeklySchedule[day] ?: false
                    val isToday = index == todayIndex

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day,
                            fontSize = 14.sp,
                            color = if (isToday) PrimaryBlue else TextSecondary,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isWorkout) Color(0xFF10B981) else Color.LightGray.copy(0.3f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isWorkout) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoSavedScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SurfaceStart, SurfaceEnd)))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Thông tin đã được lưu!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Quay lại", color = Color.White, fontSize = 18.sp)
        }
    }
}