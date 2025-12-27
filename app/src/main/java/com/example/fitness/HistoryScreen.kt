package com.example.fitness.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness.R
import com.example.fitness.entity.WorkoutSession
import com.example.fitness.viewModel.WorkoutViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val PrimaryBlue = Color(0xFF0EA5E9)
private val TextPrimary = Color(0xFF1F2937)
private val TextSecondary = Color(0xFF6B7280)
private val BgSelected = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel,
    userId: Int
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val allSessions by workoutViewModel.getHistory(userId).collectAsState(initial = emptyList())

    fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    val daysWithWorkout = remember(allSessions) {
        allSessions.map { it.date.toLocalDate() }.toSet()
    }

    val sessionsInSelectedWeek = remember(selectedDate, allSessions) {
        val startOfWeek = selectedDate.minusDays((selectedDate.dayOfWeek.value - 1).toLong())
        val endOfWeek = startOfWeek.plusDays(6)
        allSessions.filter {
            val date = it.date.toLocalDate()
            !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            CalendarSection(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                daysWithWorkout = daysWithWorkout,
                onMonthChange = { currentMonth = it },
                onDateSelected = { selectedDate = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 8.dp)
            Spacer(modifier = Modifier.height(16.dp))
            WeeklySummarySection(selectedDate, sessionsInSelectedWeek)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarSection(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    daysWithWorkout: Set<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ChevronLeft, null)
            }
            Text("${currentMonth.year}/${currentMonth.monthValue}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                Icon(Icons.Default.ChevronRight, null)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        val daysOfWeek = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            daysOfWeek.forEach { Text(it, color = TextSecondary, fontSize = 14.sp) }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val firstDayOfMonth = currentMonth.atDay(1)
        val dayOfWeekValue = firstDayOfMonth.dayOfWeek.value
        val emptyDays = if (dayOfWeekValue == 7) 0 else dayOfWeekValue
        val daysInMonth = currentMonth.lengthOfMonth()
        val totalCells = emptyDays + daysInMonth
        val rows = (totalCells + 6) / 7

        Column {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - emptyDays + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = currentMonth.atDay(dayNumber)
                            DayCell(
                                day = dayNumber,
                                isSelected = date == selectedDate,
                                hasWorkout = daysWithWorkout.contains(date),
                                onClick = { onDateSelected(date) }
                            )
                        } else {
                            Spacer(modifier = Modifier.size(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(day: Int, isSelected: Boolean, hasWorkout: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isSelected) BgSelected else if (hasWorkout) PrimaryBlue else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isSelected || hasWorkout) Color.White else TextPrimary,
            fontWeight = if (isSelected || hasWorkout) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklySummarySection(selectedDate: LocalDate, sessions: List<WorkoutSession>) {
    val startOfWeek = selectedDate.minusDays((selectedDate.dayOfWeek.value - 1).toLong())
    val endOfWeek = startOfWeek.plusDays(6)
    val formatter = DateTimeFormatter.ofPattern("d 'Th'MM")


    // Tính tổng thời gian thật từ Database (đơn vị ms)
    val totalMillis = sessions.sumOf { it.duration }
    val totalSeconds = totalMillis / 1000
    // Format thành mm:ss
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", totalSeconds / 60, totalSeconds % 60)

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Tóm Tắt Hàng Tuần", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("${startOfWeek.format(formatter)} - ${endOfWeek.format(formatter)}", fontWeight = FontWeight.Bold)
                Text("${sessions.size} Lần tập", color = TextSecondary, fontSize = 14.sp)
            }
            // Chỉ hiện thời gian, bỏ Kcal
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, Modifier.size(16.dp), PrimaryBlue)
                    Spacer(Modifier.width(4.dp))
                    Text(timeFormatted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(sessions) { session -> WorkoutHistoryItem(session) }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutHistoryItem(session: WorkoutSession) {
    val date = Instant.ofEpochMilli(session.date).atZone(ZoneId.systemDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val dayFormatter = DateTimeFormatter.ofPattern("d 'Th'MM")

    val imageRes = when (session.workoutType) {
        "Bụng Dễ",  "Plan 1" -> R.drawable.abs1

        // Plan 2: Bụng Trung bình
        "Bụng Trung bình", "Plan 2" -> R.drawable.abs2

        // Plan 3: Bụng Nâng cao
        "Bụng Nâng cao", "Plan 3" -> R.drawable.abs3

        // Plan 4: Cánh tay Dễ (hoặc tên cũ Cánh tay)
        "Cánh tay Dễ", "Cánh tay", "Plan 4" -> R.drawable.arm

        // Plan 5: Cơ bắp tay TB
        "Cơ bắp tay Trung bình", "Plan 5" -> R.drawable.hitdat1 

        // Plan 6: Ngực Dễ
        "Ngực Dễ", "Plan 6" -> R.drawable.chest1

        // Plan 7: Chân Dễ
        "Chân Dễ", "Plan 7" -> R.drawable.leg1
        else -> R.drawable.ic_launcher_background
    }

    // Lấy thời gian thật của từng bài
    val seconds = session.duration / 1000
    val durationString = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60)

    Row(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color.Gray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text("${dayFormatter.format(date)}, ${timeFormatter.format(date)}", fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(session.workoutType, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, null, Modifier.size(14.dp), PrimaryBlue)
                // Hiển thị thời gian thật
                Text(" $durationString", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}