import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

data class CaloriesRecord(
    val caloriesBurned: Float,
    val timestamp: Long = System.currentTimeMillis()
)

val CaloriesRecordListSaver: Saver<MutableList<CaloriesRecord>, Map<String, List<Any>>> = Saver(
    save = { list ->
        mapOf(
            "calories" to list.map { it.caloriesBurned.toDouble() },
            "timestamps" to list.map { it.timestamp }
        )
    },
    restore = { map ->
        val calories = map["calories"] as? List<Double> ?: emptyList()
        val timestamps = map["timestamps"] as? List<Long> ?: emptyList()
        calories.zip(timestamps) { cal, ts ->
            CaloriesRecord(cal.toFloat(), ts)
        }.toMutableStateList()
    }
)

@Composable
fun CaloriesRecordCard(
    record: CaloriesRecord,
    onDelete: (CaloriesRecord) -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val date = remember(record.timestamp) { Date(record.timestamp) }
    val formattedDate = remember(date) { sdf.format(date) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1E8E2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Lượng calo tiêu hao:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "%.2f kcal".format(record.caloriesBurned),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0288D1)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Thời gian: $formattedDate",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Button(
                onClick = { onDelete(record) },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Xóa", color = Color.White)
            }
        }
    }
}

@Composable
fun CaloriesStatisticsScreen(
    records: List<CaloriesRecord>,
    onBack: () -> Unit,
    onDeleteRecord: (CaloriesRecord) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Thống kê lượng calo tiêu hao",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0288D1),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (records.isEmpty()) {
            Text(
                "Chưa có dữ liệu thống kê",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(records, key = { it.timestamp }) { record ->
                    CaloriesRecordCard(record = record, onDelete = onDeleteRecord)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Quay về", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun CaloriesScreen(
    navController: NavHostController,
    initialCaloriesRecord: CaloriesRecord? = null
) {
    val caloriesRecords = rememberSaveable(saver = CaloriesRecordListSaver) {
        if (initialCaloriesRecord != null) {
            mutableStateListOf(initialCaloriesRecord)
        } else {
            mutableStateListOf()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        CaloriesStatisticsScreen(
            records = caloriesRecords,
            onBack = { navController.popBackStack() },
            onDeleteRecord = { record ->
                caloriesRecords.remove(record)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val newCalories = (80..200).random().toFloat()
                    caloriesRecords.add(0, CaloriesRecord(newCalories))
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Thêm bản ghi calo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Button(
                onClick = { caloriesRecords.clear() },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Xóa tất cả", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
