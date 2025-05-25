package com.example.fitness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Theo dõi nước uống",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                "Mục tiêu hàng ngày: 2000 ml",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LinearProgressIndicator(
                progress = (totalIntake.toFloat() / goal).coerceAtMost(1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Đã uống: $totalIntake ml", fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
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
                            // Giữ nguyên inputAmount cũ, không cập nhật giá trị mới vượt quá
                        }
                    },
                    label = { Text("Nhập lượng nước (ml)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        inputAmount.toIntOrNull()?.let {
                            viewModel.addRecord(it)
                            inputAmount = ""
                        }
                    },
                    enabled = inputAmount.isNotBlank()
                ) {
                    Text("Thêm")
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Lịch sử uống nước hôm nay:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))

            if (todayRecords.isEmpty()) {
                Text("Chưa có dữ liệu uống nước hôm nay", color = Color.Gray)
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
                            Text("${record.amountMl} ml")
                            Text(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(record.timestamp)))
                            IconButton(onClick = { viewModel.deleteRecord(record.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa")
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Quay lại", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
