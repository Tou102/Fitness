import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fitness.NutritionItem
import com.example.fitness.R

@Composable
fun AgeDetailsScreen(ageRange: String?, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)), // Đổi nền thành xanh dương
    ) {
        // Nội dung chính của màn hình (ở trên cùng)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Độ tuổi: $ageRange",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White // Đổi màu chữ cho dễ đọc trên nền xanh
            )
            // Thêm nội dung chi tiết về độ tuổi tại đây

        }
        // Nút "Quay về" (ở dưới cùng bên trái)
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart) // Đặt ở dưới cùng bên trái
                .padding(16.dp)
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Quay về")
            }
        }
    }
}

@Composable
fun WorkoutDetailScreen(workoutType: String?, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bài tập: $workoutType",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Thẻ thứ nhất
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Thông tin thêm 1",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Đây là một số thông tin chi tiết bổ sung về bài tập này.")
                    // Bạn có thể thêm nhiều nội dung hơn vào đây
                }
            }

        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Quay về")
            }
        }
    }
}


