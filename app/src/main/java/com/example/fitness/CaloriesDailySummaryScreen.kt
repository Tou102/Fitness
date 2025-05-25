import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
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
            .background(Color(0xFF0288D1))  // Nền xanh dương fitness
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Thống kê calo trong ngày",
            fontSize = 27.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black, // Màu đen cho chữ
            modifier = Modifier.padding(bottom = 24.dp)
        )


        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White  // Nền trong trắng
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SimplePieChart(totalCalories = totalCalories)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Tổng calories tiêu hao hôm nay: %.2f kcal".format(totalCalories),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFB8C00), // Cam đậm nhẹ nhàng
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                if (todayRecords.isEmpty()) {
                    Text(
                        "Không có dữ liệu hôm nay",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(todayRecords, key = { it.id }) { record ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)) // Nền xanh nhạt cho từng card
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Calo: %.2f kcal".format(record.caloriesBurned),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF0288D1) // Màu xanh dương cho calo
                                    )
                                    Text(
                                        sdf.format(Date(record.timestamp)),
                                        fontSize = 14.sp,
                                        color = Color(0xFF0288D1) // Màu xanh dương cho thời gian
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)), // Nền xanh dương
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Quay lại",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
@Composable
fun SimplePieChart(
    totalCalories: Float,
    goalCalories: Float = 500f,
    modifier: Modifier = Modifier
) {
    val sweepAngle = (totalCalories / goalCalories).coerceIn(0f, 1f) * 360f
    val orangeColor = Color(0xFFFFA726)  // Màu cam

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 32f
            drawArc(
                color = Color(0xFFE0E0E0), // Nền phần chưa đạt màu xám nhạt cho nổi bật trên nền trắng
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = orangeColor, // Phần calo đã đạt màu cam
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "%.0f / %.0f kcal".format(totalCalories.coerceAtMost(goalCalories), goalCalories),
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


