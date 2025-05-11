import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.MaterialTheme

@Composable
fun AgeDetailsScreen(ageRange: String?, navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Nội dung chính của màn hình (ở trên cùng)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(text = "Age Details for: $ageRange", style = MaterialTheme.typography.headlineMedium)
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
        modifier = Modifier.fillMaxSize(),
    ) {
        // Nội dung chính của màn hình (ở trên cùng)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(text = "Workout Details for: $workoutType", style = MaterialTheme.typography.headlineMedium)
            // Thêm nội dung chi tiết về bài tập tại đây
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
