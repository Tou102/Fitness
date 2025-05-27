import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BmiScreen(navController: NavController) {
    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Nam") }
    var trainingGoal by remember { mutableStateOf("Tăng cân") }
    var exerciseLevel by remember { mutableStateOf("Trung bình") }
    var ageRange by remember { mutableStateOf("18-25") }

    var bmiResult by remember { mutableStateOf<Double?>(null) }
    var bmiStatus by remember { mutableStateOf("") }
    var heightError by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }

    val genders = listOf("Nam", "Nữ")
    val exerciseLevels = listOf("Ít vận động", "Trung bình", "Thường xuyên")
    val ageRanges = listOf("<18", "18-25", "26-35", "36-50", ">50")
    val goals = listOf("Tăng cân", "Giảm cân", "Giảm mỡ", "Giữ vóc dáng")

    fun calculateBmi(weight: Double, height: Double): Double {
        return weight / (height * height)
    }

    fun getBmiStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Gầy"
            bmi < 24.9 -> "Bình thường"
            bmi < 29.9 -> "Thừa cân"
            else -> "Béo phì"
        }
    }

    // Dùng LazyColumn để cuộn được khi form dài
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF31E9FE5))  // Nền xanh dương đậm hơn
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Thông tin cơ bản",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            DropdownMenuBox(
                label = "Độ tuổi",
                selected = ageRange,
                options = ageRanges,
                onSelectedChange = { ageRange = it },
                textColor = Color.White
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Giới tính:", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(16.dp))
                genders.forEach { gender ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.Green)
                        )
                        Text(gender, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = weightInput,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) weightInput = it },
                label = { Text("Cân nặng (kg)", color = Color.White, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
        }

        item {
            OutlinedTextField(
                value = heightInput,
                onValueChange = {
                    heightError = false
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) heightInput = it
                },
                isError = heightError,
                label = { Text("Chiều cao (m)", color = Color.White, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            if (heightError) {
                Text("Chiều cao không hợp lệ (nên dưới 2.5m)", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        item {
            DropdownMenuBox(
                label = "Mục tiêu luyện tập",
                selected = trainingGoal,
                options = goals,
                onSelectedChange = { trainingGoal = it },
                textColor = Color.White
            )
        }

        item {
            DropdownMenuBox(
                label = "Cường độ luyện tập",
                selected = exerciseLevel,
                options = exerciseLevels,
                onSelectedChange = { exerciseLevel = it },
                textColor = Color.White
            )
        }

        item {
            Button(
                onClick = {
                    val weight = weightInput.toDoubleOrNull()
                    val height = heightInput.toDoubleOrNull()
                    if (weight != null && height != null && height in 0.5..2.5) {
                        val bmi = calculateBmi(weight, height)
                        bmiResult = bmi
                        bmiStatus = getBmiStatus(bmi)
                        showResultDialog = true
                        heightError = false
                    } else {
                        heightError = true
                        showResultDialog = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)) // xanh lá đậm
            ) {
                Text("Tính BMI", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp)) // Tạo khoảng cách đáy để dễ cuộn
        }

        item {
            Button(
                onClick = {
                    if (weightInput.isNotEmpty() && heightInput.isNotEmpty()) {
                        navController.navigate("workout?goal=$trainingGoal")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)) // xanh dương đậm
            ) {
                Text("Bắt đầu theo dõi", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Dialog hiện kết quả BMI
    if (showResultDialog && bmiResult != null) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("Kết quả BMI", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("BMI: ${String.format("%.2f", bmiResult)}", fontWeight = FontWeight.Bold)
                    Text("Phân loại: $bmiStatus", fontWeight = FontWeight.Bold)
                    Text("Giới tính: $selectedGender", fontWeight = FontWeight.Bold)
                    Text("Độ tuổi: $ageRange", fontWeight = FontWeight.Bold)
                    Text("Mục tiêu: $trainingGoal", fontWeight = FontWeight.Bold)
                    Text("Cường độ luyện tập: $exerciseLevel", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    label: String,
    selected: String,
    options: List<String>,
    onSelectedChange: (String) -> Unit,
    textColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp) // Tăng chiều cao dropdown box
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = textColor, fontWeight = FontWeight.Bold) },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(
                            selectionOption,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = {
                        onSelectedChange(selectionOption)
                        expanded = false
                    },
                    modifier = Modifier.height(56.dp) // Tăng chiều cao của item trong dropdown
                )
            }
        }
    }
}