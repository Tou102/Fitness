package com.example.fitness.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fitness.db.AppDatabase
import com.example.fitness.entity.DailyGoalEntity
import com.example.fitness.entity.RunningChallengeEntity
import com.example.fitness.ui.theme.*
import com.example.fitness.viewModel.ChallengeViewModel
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val challengeViewModel: ChallengeViewModel = viewModel(
        factory = ChallengeViewModel.factory(database.challengeDao())
    )

    val activeChallenge by challengeViewModel.activeChallenge.collectAsStateWithLifecycle()
    val completedDays by challengeViewModel.completedDays.collectAsStateWithLifecycle()
    val todayGoal by challengeViewModel.todayGoal.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEndDialog by remember { mutableStateOf(false) }

    var remainingTime by remember { mutableStateOf("Đang tính...") }

    // Cập nhật thời gian còn lại realtime
    LaunchedEffect(activeChallenge) {
        while (true) {
            remainingTime = challengeViewModel.getRemainingTime()
            delay(60_000L) // cập nhật mỗi phút
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SurfaceStart, SurfaceEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Filled.RunCircle,
                contentDescription = null,
                tint = PrimaryBlue.copy(alpha = 0.85f),
                modifier = Modifier.size(88.dp)
            )

            Text(
                "Thử thách cá nhân",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            Text(
                "Duy trì thói quen – Chinh phục chính mình",
                fontSize = 16.sp,
                color = AccentBlue.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(40.dp))

            AnimatedVisibility(
                visible = activeChallenge != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                activeChallenge?.let { challenge ->
                    ActiveChallengeCard(
                        challenge = challenge,
                        completedDays = completedDays,
                        todayGoal = todayGoal,
                        remainingTime = remainingTime,
                        onEndChallenge = { showEndDialog = true }
                    )
                }
            }

            AnimatedVisibility(
                visible = activeChallenge == null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                NoChallengeView(onCreateNew = { showCreateDialog = true })
            }

            Spacer(Modifier.weight(1f))
        }

        // Dialogs
        if (showCreateDialog) {
            CreateChallengeDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, days, template ->
                    challengeViewModel.createNewChallenge(name, days, template, context)
                    showCreateDialog = false
                    navController.navigate("running") {
                        popUpTo("challenge") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        if (showEndDialog) {
            AlertDialog(
                onDismissRequest = { showEndDialog = false },
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        "Kết thúc thử thách sớm?",
                        fontWeight = FontWeight.Bold,
                        color = WarningRed
                    )
                },
                text = {
                    Text(
                        "Bạn có chắc chắn muốn thừa nhận thất bại?\n" +
                                "Tất cả tính năng sẽ được mở lại bình thường."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        challengeViewModel.endChallengeEarly(context)
                        showEndDialog = false
                        navController.navigate("leaderboard") {
                            popUpTo("challenge") { inclusive = true }
                        }
                    }) {
                        Text("Xác nhận thất bại", color = WarningRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
fun ActiveChallengeCard(
    challenge: RunningChallengeEntity,
    completedDays: Int,
    todayGoal: DailyGoalEntity?,
    remainingTime: String,
    onEndChallenge: () -> Unit
) {
    fun getCurrentDayNumber(): Int {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        val start = challenge.startDate
        val diff = today - start
        return if (diff <= 0) 1 else ((diff / (86_400_000)) + 1).toInt().coerceAtMost(challenge.totalDays)
    }

    val currentDay = getCurrentDayNumber()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Flag,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    challenge.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Thời gian còn lại", fontSize = 14.sp, color = TextSecondary)
                    Text(
                        remainingTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentBlue
                    )
                }
                Text(
                    "Ngày $currentDay / ${challenge.totalDays}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
            }

            Spacer(Modifier.height(16.dp))

            if (todayGoal != null) {
                Text("Mục tiêu hôm nay", fontSize = 14.sp, color = TextSecondary)
                Text(
                    todayGoal.description,
                    fontSize = 16.sp,
                    color = AccentBlue,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(16.dp))
            }

            LinearProgressIndicator(
                progress = { completedDays.toFloat() / challenge.totalDays },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = SuccessGreen,
                trackColor = Color(0xFFE0E0E0),
                strokeCap = StrokeCap.Round
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "$completedDays ngày hoàn thành",
                fontSize = 16.sp,
                color = SuccessGreen,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onEndChallenge,
                colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    "Kết thúc thử thách (Thừa nhận thất bại)",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun NoChallengeView(onCreateNew: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.97f)),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.RunCircle,
                contentDescription = null,
                tint = PrimaryBlue.copy(alpha = 0.7f),
                modifier = Modifier.size(100.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Bạn chưa có thử thách nào",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Tạo thử thách 50–100 ngày để xây dựng thói quen chạy bộ bền vững và chinh phục bản thân!",
                fontSize = 16.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onCreateNew,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(64.dp)
                    .shadow(12.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Bắt đầu thử thách ngay",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CreateChallengeDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, days: Int, template: String) -> Unit
) {
    var selectedDays by remember { mutableIntStateOf(90) }
    var selectedTemplate by remember { mutableStateOf("gain_muscle_fat_loss") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var aiSuggestion by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        if (uri != null && selectedTemplate == "custom") {
            aiSuggestion = "Đang phân tích ảnh InBody... (kết nối AI)"
            // TODO: Gọi AI thật để gợi ý (tên, ngày, bài tập hàng ngày)
        }
    }

    val templates = listOf(
        "gain_muscle_fat_loss" to "Tăng cơ - Giảm mỡ",
        "gain_weight" to "Tăng cân",
        "lose_weight" to "Giảm cân, thon gọn",
        "custom" to "Tùy chỉnh – Chọn ảnh InBody để AI gợi ý mục tiêu + bài tập mỗi ngày phù hợp"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Text(
                "Tạo thử thách mới",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = PrimaryBlue
            )
        },
        text = {
            Column {
                Text("Chọn mục tiêu & bài tập hàng ngày:", fontSize = 16.sp, color = Color(0xFF444444))

                Spacer(Modifier.height(16.dp))

                templates.forEach { (key, desc) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { selectedTemplate = key },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTemplate == key,
                            onClick = { selectedTemplate = key },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryBlue,
                                unselectedColor = Color(0xFF888888)
                            )
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(desc, fontSize = 14.sp, color = Color(0xFF333333), lineHeight = 20.sp)
                    }
                }

                if (selectedTemplate == "custom") {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Chọn ảnh InBody để AI gợi ý", color = Color.White)
                    }

                    selectedImageUri?.let { uri ->
                        Spacer(Modifier.height(12.dp))
                        AsyncImage(
                            model = uri,
                            contentDescription = "Ảnh InBody",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (aiSuggestion.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "AI gợi ý: $aiSuggestion",
                            fontSize = 14.sp,
                            color = AccentBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Số ngày: $selectedDays ngày",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentBlue
                )

                Slider(
                    value = selectedDays.toFloat(),
                    onValueChange = { selectedDays = it.toInt().coerceIn(30, 120) },
                    valueRange = 30f..120f,
                    steps = 89,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryBlue,
                        activeTrackColor = PrimaryBlue,
                        inactiveTrackColor = Color(0xFFE0E0E0)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val name = when (selectedTemplate) {
                    "gain_muscle_fat_loss" -> "Tăng Cơ - Giảm Mỡ $selectedDays Ngày"
                    "gain_weight" -> "Tăng Cân Lành Mạnh $selectedDays Ngày"
                    "lose_weight" -> "Giảm Cân Hiệu Quả $selectedDays Ngày"
                    else -> "Thử Thách Tùy Chỉnh $selectedDays Ngày"
                }
                onCreate(name, selectedDays, selectedTemplate)
            }) {
                Text("Bắt đầu ngay", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = Color(0xFF666666))
            }
        }
    )
}