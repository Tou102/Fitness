import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BieuDo(navController: NavHostController) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf<Float?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Cân nặng (kg)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Chiều cao (m)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )

        Button(
            onClick = {
                val w = weight.toFloatOrNull()
                val h = height.toFloatOrNull()
                if (w != null && h != null && h > 0f) {
                    bmiResult = w / (h * h)
                } else {
                    bmiResult = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tính BMI")
        }

        if (bmiResult != null) {
            Text(text = "Chỉ số BMI của bạn là: %.2f".format(bmiResult))
        }
    }
}
