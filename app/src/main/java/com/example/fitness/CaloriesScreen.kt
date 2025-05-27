import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.fitness.entity.CaloriesRecordEntity
import com.example.fitness.repository.CaloriesRepository
import com.example.fitness.viewModel.CaloriesViewModel


import com.example.fitness.viewModelFactory.CaloriesViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CaloriesRecordCard(
    record: CaloriesRecordEntity,
    onDelete: (CaloriesRecordEntity) -> Unit
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
    records: List<CaloriesRecordEntity>,
    onBack: () -> Unit,
    onDeleteRecord: (CaloriesRecordEntity) -> Unit
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
                items(records, key = { it.id }) { record ->
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
fun CaloriesScreenWithViewModel(
    navController: NavHostController,
    context: Context
) {
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CaloriesRepository(db.caloriesRecordDao()) }
    val factory = remember { CaloriesViewModelFactory(repository) }
    val viewModel: CaloriesViewModel = viewModel(factory = factory)

    CaloriesScreen(
        navController = navController,
        caloriesViewModel = viewModel
    )
}


// CaloriesScreen.kt (hoặc trong file UI bạn có màn CaloriesScreen)
@Composable
fun CaloriesScreen(
    navController: NavHostController,
    caloriesViewModel: CaloriesViewModel
) {
    val records by caloriesViewModel.records.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        CaloriesStatisticsScreen(
            records = records,
            onBack = { navController.popBackStack() },
            onDeleteRecord = { record -> caloriesViewModel.deleteRecord(record) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newRecord = CaloriesRecordEntity(
                    caloriesBurned = (80..200).random().toFloat(),
                    timestamp = System.currentTimeMillis(),
                    distance = 0f,
                    runningTime = 0L
                )
                caloriesViewModel.addRecord(newRecord)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Thêm bản ghi calo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}





