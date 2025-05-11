package com.example.fitness

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun NutritionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Chế độ dinh dưỡng",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(nutritionItems) { item ->
                NutritionCard(item = item, onClick = {
                    when (item.title) {
                        "Uống nước" -> navController.navigate("water_detail")
                        "Rau củ" -> navController.navigate("vegetable_detail")
                        else -> {} // Hoặc xử lý các mục khác sau
                    }
                })
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Quay lại")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Tiếp tục")
            }
        }
    }
}
@Composable
fun NutritionCard(item: NutritionItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}


data class NutritionItem(val title: String, val imageResId: Int)
val nutritionItems = listOf(
    NutritionItem("Uống nước", R.drawable.nuocep),
    NutritionItem("Rau củ", R.drawable.raucu),
    NutritionItem("Hạn chế đường", R.drawable.thom),
    NutritionItem("Protein", R.drawable.thitbo),
    NutritionItem("Ăn sáng", R.drawable.thitheo),
)