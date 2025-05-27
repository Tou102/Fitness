package com.example.fitness

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitness.entity.CaloriesRecordEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CaloriesDailySummaryScreen(
    records: List<CaloriesRecordEntity>,
    navController: NavHostController,
    onBack: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val startOfDay = calendar.timeInMillis
    val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1

    val todayRecords = records.filter { it.timestamp in startOfDay..endOfDay }
    val totalCalories = todayRecords.sumOf { it.caloriesBurned.toDouble() }.toFloat()
    val sdf = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0288D1), // Blue
                        Color(0xFF4FC3F7) // Lighter blue
                    )
                )
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Thống kê calo trong ngày",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EnhancedPieChart(totalCalories = totalCalories)

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    "Tổng calo tiêu hao hôm nay: %.2f kcal".format(totalCalories),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFB8C00),
                    modifier = Modifier.padding(bottom = 28.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                if (todayRecords.isEmpty()) {
                    Text(
                        "Không có dữ liệu hôm nay",
                        fontSize = 20.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(todayRecords, key = { it.id }) { record ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(animateFloatAsState(
                                        targetValue = 1f,
                                        animationSpec = tween(300),
                                        label = "cardScale"
                                    ).value)
                                    .shadow(8.dp, RoundedCornerShape(18.dp))
                                    .clip(RoundedCornerShape(18.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFFBBDEFB),
                                                    Color(0xFF90CAF9)
                                                )
                                            )
                                        )
                                        .padding(20.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Calo: %.2f kcal".format(record.caloriesBurned),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF0288D1)
                                        )
                                        Text(
                                            sdf.format(Date(record.timestamp)),
                                            fontSize = 16.sp,
                                            color = Color(0xFF0288D1)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(36.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .scale(animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = tween(300),
                            label = "buttonScale"
                        ).value)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0288D1),
                                        Color(0xFF4FC3F7)
                                    )
                                )
                            )
                    ) {
                        Text(
                            "Quay lại",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedPieChart(
    totalCalories: Float,
    goalCalories: Float = 500f,
    modifier: Modifier = Modifier
) {
    val progress = (totalCalories / goalCalories).coerceIn(0f, 1f)
    val sweepAngle = progress * 360f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1200),
        label = "chartProgress"
    )
    val animatedSweepAngle = animatedProgress * 360f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .shadow(10.dp, RoundedCornerShape(50))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFE6F0FA) // Light blue
                        )
                    ),
                    RoundedCornerShape(50)
                )
                .border(4.dp, Color(0xFF0288D1), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(160.dp)
            ) {
                // Background arc
                drawArc(
                    color = Color(0xFFE0E0E0),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 28f)
                )
                // Progress arc with gradient
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFA726), // Orange
                            Color(0xFFFFD54F) // Yellow
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedSweepAngle,
                    useCenter = false,
                    style = Stroke(width = 28f)
                )
                // Center circle
                drawCircle(
                    color = Color.White,
                    radius = size.minDimension / 4
                )
            }
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0288D1),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "%.0f / %.0f kcal".format(totalCalories.coerceAtMost(goalCalories), goalCalories),
            fontSize = 22.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}