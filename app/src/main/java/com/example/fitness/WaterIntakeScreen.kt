package com.example.fitness

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitness.db.AppDatabase
import com.example.fitness.repository.WaterIntakeRepository
import com.example.fitness.viewModel.WaterIntakeViewModel
import com.example.fitness.viewModelFactory.WaterIntakeViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WaterIntakeScreen(
    navController: NavHostController,
    db: AppDatabase
) {
    val viewModel: WaterIntakeViewModel = viewModel(
        factory = WaterIntakeViewModelFactory(WaterIntakeRepository(db.waterIntakeDao()))
    )

    val records by viewModel.records.collectAsState()
    val calendar = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
    val startOfDay = calendar.timeInMillis
    val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1
    val todayRecords = records.filter { it.timestamp in startOfDay..endOfDay }
    val totalIntake = todayRecords.sumOf { it.amountMl }
    var inputAmount by remember { mutableStateOf("") }
    val goal = 2000

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(totalIntake) {
        if (totalIntake >= goal) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Bạn đã uống đủ nước hôm nay!")
            }
        }
    }

    // Animation for progress bar
    val animatedProgress by animateFloatAsState(
        targetValue = (totalIntake.toFloat() / goal).coerceAtMost(1f),
        animationSpec = tween(durationMillis = 1000), // Smooth 1-second animation
        label = "progressAnimation"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .background(Color(0xFFEDE6E6)), // Keep light gray background
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Theo dõi nước uống",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.Black, // Changed to black
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                "Mục tiêu hàng ngày: 2000 ml",
                fontSize = 18.sp,
                color = Color.Black, // Changed to black
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Custom Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFB0BEC5), Color(0xFFE0E0E0)), // Gray gradient for track
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0288D1), // Blue
                                    Color(0xFF4FC3F7) // Cyan
                                )
                            )
                        )
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Đã uống: $totalIntake ml",
                fontSize = 18.sp,
                color = Color.Black, // Changed to black
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                ) {
                    BasicTextField(
                        value = inputAmount,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter(Char::isDigit)
                            val number = filtered.toIntOrNull()
                            if (number == null) {
                                inputAmount = ""
                            } else if (number <= 2000) {
                                inputAmount = filtered
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Lượng nước nhập không được vượt quá 2000 ml!")
                                }
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black, // Changed to black
                            fontWeight = FontWeight.Bold
                        ),
                        singleLine = true,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Button(
                    onClick = {
                        inputAmount.toIntOrNull()?.let {
                            viewModel.addRecord(it)
                            inputAmount = ""
                        }
                    },
                    enabled = inputAmount.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0288D1), // Blue for button
                        contentColor = Color.Black // Black text for button
                    )
                ) {
                    Text("Thêm", color = Color.Black) // Changed to black
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Lịch sử uống nước hôm nay:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black // Changed to black
            )
            Spacer(Modifier.height(8.dp))

            if (todayRecords.isEmpty()) {
                Text("Chưa có dữ liệu uống nước hôm nay", color = Color.Black) // Changed to black
            } else {
                LazyColumn {
                    items(todayRecords) { record ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${record.amountMl} ml",
                                color = Color.Black, // Changed to black
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(record.timestamp)),
                                color = Color.Black // Changed to black
                            )
                            IconButton(onClick = { viewModel.deleteRecord(record.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1), // Blue
                    contentColor = Color.Black // Black text
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Quay lại", color = Color.Black, fontWeight = FontWeight.Bold) // Changed to black
            }
        }
    }
}