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

    val genders = listOf("Nam", "Nữ")
    val exerciseLevels = listOf("Ít vận động", "Trung bình", "Thường xuyên")
    val ageRanges = listOf("<18", "18-25", "26-35", "36-50", ">50")
    val goals = listOf("Tăng cân", "Giảm cân", "Giảm mỡ", "Giữ vóc dáng")

    fun calculateBmi(weight: Double, height: Double): Double = weight / (height * height)

    fun getBmiStatus(bmi: Double): String = when {
        bmi < 18.5 -> "Gầy"
        bmi < 24.9 -> "Bình thường"
        else -> "Thừa cân"  // Gộp cả béo phì vào đây
    }


    fun getBodyImageResource(bmiStatus: String): Int {
        return when (bmiStatus) {
            "Gầy" -> R.drawable.gaymo
            "Bình thường"-> R.drawable.bthfat  // Gộp cả hai loại này
            else -> R.drawable.mapfat // fallback để tránh lỗi compile
        }
    }





    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFD0E8FF), Color(0xFFFFFFFF))
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                "Thông tin cơ bản",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                color = Color(0xFF0D47A1),
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 12.dp).shadow(6.dp, RoundedCornerShape(12.dp))
            )
        }
        item {
            DropdownMenuBox("Độ tuổi", ageRange, ageRanges, { ageRange = it }, Color(0xFF0D47A1))
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Giới tính:", color = Color(0xFF0D47A1), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                genders.forEach { gender ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(Color(0xFF4CAF50).copy(alpha = 0.1f), RoundedCornerShape(12.dp)).padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF388E3C), unselectedColor = Color.Black)
                        )
                        Text(gender, color = Color(0xFF0D47A1), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = weightInput,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) weightInput = it },
                label = { Text("Cân nặng (kg)", color = Color(0xFF0D47A1), fontWeight = FontWeight.ExtraBold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(12.dp)).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.95f)),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF0D47A1), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
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
                label = { Text("Chiều cao (m)", color = Color(0xFF0D47A1), fontWeight = FontWeight.ExtraBold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(12.dp)).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.95f)),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF0D47A1), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            )
            if (heightError) {
                Text("Chiều cao không hợp lệ (nên dưới 2.5m)", color = Color.Red, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
        item {
            DropdownMenuBox("Mục tiêu luyện tập", trainingGoal, goals, { trainingGoal = it }, Color(0xFF0D47A1))
        }
        item {
            DropdownMenuBox("Cường độ luyện tập", exerciseLevel, exerciseLevels, { exerciseLevel = it }, Color(0xFF0D47A1))
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
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2), contentColor = Color.White)
            ) {
                Text("Tính BMI", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }

    if (showResultDialog && bmiResult != null) {
        val scale by animateFloatAsState(
            targetValue = if (showResultDialog) 1f else 0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )

        val imageRes = getBodyImageResource(bmiStatus)

        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            modifier = Modifier.scale(scale).background(Color.White, RoundedCornerShape(24.dp)),
            title = {
                Text("Kết quả BMI", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF0D47A1), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Body illustration",
                        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)).shadow(4.dp)
                    )
                    Text("BMI: ${String.format("%.2f", bmiResult)}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0D47A1))
                    Text("Phân loại: $bmiStatus", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0D47A1))
                    Text("Giới tính: $selectedGender", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0D47A1))
                    Text("Độ tuổi: $ageRange", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0D47A1))
                    Text("Mục tiêu: $trainingGoal", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0D47A1))
                    Text("Cường độ luyện tập: $exerciseLevel", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0D47A1))
                }
            },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }, modifier = Modifier.fillMaxWidth()) {
                    Text("Đóng", color = Color(0xFF0D47A1), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
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
        modifier = Modifier.fillMaxWidth().height(76.dp).shadow(6.dp, RoundedCornerShape(12.dp))
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp) },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.95f)),
            textStyle = LocalTextStyle.current.copy(color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFF0D47A1),
                unfocusedIndicatorColor = Color(0xFF1976D2),
                focusedLabelColor = Color(0xFF0D47A1),
                unfocusedLabelColor = Color(0xFF1976D2)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Brush.verticalGradient(colors = listOf(Color(0xFFD0E8FF), Color(0xFFFFFFFF))))
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(selectionOption, fontSize = 18.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.ExtraBold)
                    },
                    onClick = {
                        onSelectedChange(selectionOption)
                        expanded = false
                    },
                    modifier = Modifier.height(60.dp).fillMaxWidth()
                )
            }
        }
    }
}
