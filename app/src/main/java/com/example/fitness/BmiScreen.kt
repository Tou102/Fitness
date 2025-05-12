import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BmiScreen(navController: NavController) {
    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Chỉ số cơ thể",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nhập cân nặng
        OutlinedTextField(
            value = weightInput,
            onValueChange = {
                // Chỉ cho phép nhập số và dấu chấm
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    weightInput = it
                }
            },
            label = { Text("Cân nặng (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nhập chiều cao
        OutlinedTextField(
            value = heightInput,
            onValueChange = {
                // Chỉ cho phép nhập số
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    heightInput = it
                }
            },
            label = { Text("Chiều cao (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    // Kiểm tra xem đã nhập cân nặng và chiều cao chưa trước khi chuyển hướng
                    if (weightInput.isNotEmpty() && heightInput.isNotEmpty()) {
                        // Chuyển hướng sang màn hình theo dõi bài tập
                        navController.navigate("workout") // Thay "workoutTracking" bằng route thực tế
                    } else {
                        // Hiển thị thông báo lỗi hoặc thực hiện hành động khác nếu cần
                        // Ví dụ: Toast.makeText(context, "Vui lòng nhập cân nặng và chiều cao", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green) // Đổi màu nút cho dễ phân biệt
            ) {
                Text("Theo dõi bài tập", color = Color.White)
            }
        }
    }
}