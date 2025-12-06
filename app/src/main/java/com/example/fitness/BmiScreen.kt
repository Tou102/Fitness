package com.example.fitness

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var predictionResult by remember { mutableStateOf("") }

    val genders = listOf("Nam", "Nữ")
    val exerciseLevels = listOf("Ít vận động", "Trung bình", "Thường xuyên")
    val ageRanges = listOf("<18", "18-25", "26-35", "36-50", ">50")
    val goals = listOf("Tăng cân", "Giảm cân", "Giảm mỡ", "Giữ vóc dáng")

    fun calculateBmi(weight: Double, height: Double): Double = weight / (height * height)

    fun getBmiStatus(bmi: Double): String = when {
        bmi < 18.5 -> "Gầy"
        bmi < 24.9 -> "Bình thường"
        else -> "Thừa cân"
    }

    // --------------------------
    // SHOW IMAGE BASED ON GENDER
    // --------------------------
    fun getBodyImageResource(status: String, gender: String): Int {
        return if (gender == "Nữ") {
            when (status) {
                "Gầy" -> R.drawable.gay
                "Bình thường" -> R.drawable.candoi
                else -> R.drawable.fat
            }
        } else {
            when (status) {
                "Gầy" -> R.drawable.gaymo
                "Bình thường" -> R.drawable.bthfat
                else -> R.drawable.mapfat
            }
        }
    }

    fun predictTrainingResult(bmi: Double, goal: String, exerciseLevel: String): String {

        // 1. Base activity factor (higher = đốt calo nhanh hơn)
        val activityFactor = when (exerciseLevel) {
            "Ít vận động" -> 0.8       // khó giảm cân
            "Trung bình" -> 1.0       // mức chuẩn
            else -> 1.25              // giảm nhanh hơn
        }

        // 2. Body condition: BMI cao giảm nhanh hơn
        val bmiFactor = when {
            bmi >= 30 -> 1.4          // béo phì giảm nhanh (khoa học)
            bmi >= 25 -> 1.2          // thừa cân
            bmi >= 18.5 -> 1.0        // bình thường
            else -> 0.6               // gầy → tăng cân khó
        }

        val finalFactor = activityFactor * bmiFactor

        return when (goal) {

            // ---------------- GIẢM MỠ / GIẢM CÂN ----------------
            "Giảm cân", "Giảm mỡ" -> {
                // số kg có thể giảm mỗi tuần
                val weeklyLoss = (0.4 * finalFactor).coerceIn(0.3, 1.5)
                val daysFor1Kg = (7 / weeklyLoss).toInt()

                "Với cường độ hiện tại, bạn có thể giảm khoảng ${"%.1f".format(weeklyLoss)} kg/tuần, " +
                        "tương đương $daysFor1Kg ngày để giảm 1kg."
            }

            // ---------------- TĂNG CÂN ----------------
            "Tăng cân" -> {
                val weeklyGain = (0.25 * activityFactor).coerceIn(0.1, 0.4)
                val daysFor05Kg = (7 / weeklyGain / 2).toInt()

                "Bạn có thể tăng khoảng **${"%.1f".format(weeklyGain)} kg/tuần**, " +
                        "tương đương $daysFor05Kg ngày để tăng 0.5kg."
            }

            // ---------------- GIỮ DÁNG ----------------
            else -> {
                val improvementDays = (28 / finalFactor).toInt()
                "Nếu duy trì đều đặn, bạn sẽ thấy cơ thể thay đổi rõ rệt sau $improvementDays ngày."
            }
        }
    }


    // UI --------------------------------------

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFD0E8FF), Color.White)
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Text(
                "Thông tin cơ bản",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D47A1),
                modifier = Modifier.shadow(6.dp, RoundedCornerShape(12.dp))
            )
        }

        item { DropdownMenuBox("Độ tuổi", ageRange, ageRanges, { ageRange = it }, Color(0xFF0D47A1)) }

        // Giới tính
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Giới tính:", fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))

                genders.forEach { gender ->
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF4CAF50).copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender }
                        )
                        Text(gender, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    }
                }
            }
        }

        // Weight input
        item {
            OutlinedTextField(
                value = weightInput,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*\$"))) weightInput = it },
                label = { Text("Cân nặng (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Height input
        item {
            OutlinedTextField(
                value = heightInput,
                onValueChange = {
                    if (it.matches(Regex("^\\d*\\.?\\d*\$"))) heightInput = it
                },
                isError = heightError,
                label = { Text("Chiều cao (m)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (heightError) {
                Text("Chiều cao không hợp lệ!", color = Color.Red)
            }
        }

        // Goals dropdown
        item { DropdownMenuBox("Mục tiêu", trainingGoal, goals, { trainingGoal = it }, Color(0xFF0D47A1)) }

        // Exercise level
        item { DropdownMenuBox("Cường độ luyện tập", exerciseLevel, exerciseLevels, { exerciseLevel = it }, Color(0xFF0D47A1)) }

        // Button
        item {
            Button(
                onClick = {
                    val w = weightInput.toDoubleOrNull()
                    val h = heightInput.toDoubleOrNull()

                    if (w != null && h != null && h in 0.5..2.5) {
                        val bmi = calculateBmi(w, h)
                        bmiResult = bmi
                        bmiStatus = getBmiStatus(bmi)
                        predictionResult = predictTrainingResult(bmi, trainingGoal, exerciseLevel)
                        showResultDialog = true
                        heightError = false
                    } else heightError = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF1976D2))
            ) {
                Text("Tính BMI", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }

    // RESULT DIALOG --------------------------------------------------

    if (showResultDialog && bmiResult != null) {

        val scale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )

        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            modifier = Modifier.scale(scale),
            title = {
                Text(
                    "Kết quả BMI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {

                    // IMAGE (changes by gender)
                    Image(
                        painter = painterResource(
                            id = getBodyImageResource(
                                bmiStatus,
                                selectedGender
                            )
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.65f)
                            .clip(RoundedCornerShape(20.dp))
                            .shadow(8.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("BMI: ${"%.2f".format(bmiResult!!)}", fontWeight = FontWeight.Bold)
                    Text("Phân loại: $bmiStatus", fontWeight = FontWeight.Bold)
                    Text("Giới tính: $selectedGender", fontWeight = FontWeight.Bold)
                    Text("Độ tuổi: $ageRange", fontWeight = FontWeight.Bold)
                    Text("Mục tiêu: $trainingGoal", fontWeight = FontWeight.Bold)
                    Text("Cường độ: $exerciseLevel", fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Dự đoán: $predictionResult",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF0D47A1)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) {
                    Text("Đóng", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// DROPDOWN -------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    label: String,
    selected: String,
    options: List<String>,
    onChange: (String) -> Unit,
    textColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = textColor) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, fontWeight = FontWeight.Bold) },
                    onClick = {
                        onChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
