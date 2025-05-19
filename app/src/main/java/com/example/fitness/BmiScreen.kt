import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val genders = listOf("Nam", "Nữ", "Khác")
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Thông tin cơ bản", style = MaterialTheme.typography.headlineSmall)

        DropdownMenuBox(
            label = "Độ tuổi",
            selected = ageRange,
            options = ageRanges,
            onSelectedChange = { ageRange = it }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Giới tính:")
            Spacer(modifier = Modifier.width(16.dp))
            genders.forEach { gender ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedGender == gender,
                        onClick = { selectedGender = gender }
                    )
                    Text(gender)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        OutlinedTextField(
            value = weightInput,
            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) weightInput = it },
            label = { Text("Cân nặng (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = heightInput,
            onValueChange = {
                heightError = false
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) heightInput = it
            },
            isError = heightError,
            label = { Text("Chiều cao (m)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        if (heightError) {
            Text("Chiều cao không hợp lệ (nên dưới 2.5m)", color = Color.Red)
        }

        DropdownMenuBox(
            label = "Mục tiêu luyện tập",
            selected = trainingGoal,
            options = goals,
            onSelectedChange = { trainingGoal = it }
        )

        DropdownMenuBox(
            label = "Cường độ luyện tập",
            selected = exerciseLevel,
            options = exerciseLevels,
            onSelectedChange = { exerciseLevel = it }
        )

        Button(
            onClick = {
                val weight = weightInput.toDoubleOrNull()
                val height = heightInput.toDoubleOrNull()
                if (weight != null && height != null && height in 0.5..2.5) {
                    val bmi = calculateBmi(weight, height)
                    bmiResult = bmi
                    bmiStatus = getBmiStatus(bmi)
                } else {
                    heightError = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Tính BMI", color = Color.White)
        }

        bmiResult?.let { bmi ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Kết quả BMI: ${String.format("%.2f", bmi)}")
                    Text("Phân loại: $bmiStatus")
                    Text("Giới tính: $selectedGender")
                    Text("Độ tuổi: $ageRange")
                    Text("Mục tiêu: $trainingGoal")
                    Text("Tập luyện: $exerciseLevel")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (weightInput.isNotEmpty() && heightInput.isNotEmpty()) {
                    navController.navigate("workout?goal=$trainingGoal")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Bắt đầu theo dõi", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    label: String,
    selected: String,
    options: List<String>,
    onSelectedChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
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
                    text = { Text(selectionOption) },
                    onClick = {
                        onSelectedChange(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}