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
                NutritionCard(item = item, navController = navController)
            }
        }

        Row( // Thêm Row để chứa hai nút
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Để các nút ở hai đầu
        ) {
            Button(
                onClick = { navController.navigate("home") }, // Điều hướng về "home"
                modifier = Modifier.weight(1f) // Chia đều không gian
            ) {
                Text("Quay lại")
            }

            Spacer(modifier = Modifier.width(16.dp)) // Thêm khoảng cách giữa hai nút

            Button(
                onClick = { navController.navigate("profile") }, // Điều hướng đến "profile"
                modifier = Modifier.weight(1f) // Chia đều không gian
            ) {
                Text("Tiếp tục")
            }
        }
    }
}


@Composable
fun NutritionCard(item: NutritionItem, navController: NavHostController) { // Thêm navController làm tham số
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { // Thêm modifier clickable
                when (item.title) {
                    "1. Chế độ ăn lỏng" -> navController.navigate("anlong_detail")
                    "2. Chế độ ăn kiêng cho người tiểu đường" -> navController.navigate("ankieng_detail")
                    "3. Chế độ dinh dưỡng giàu calo" -> navController.navigate("calo_detail")
                    "4. Chế độ ăn ít cholesterol" -> navController.navigate("choles_detail")
                    "5. Chế độ ăn chay" -> navController.navigate("anchay_detail")
                    "6. Chế độ ăn ít natri" -> navController.navigate("natri_detail")
                    "7. Chế độ dinh dưỡng ít và giàu protein" -> navController.navigate("protein_detail")


                }
            },
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
    NutritionItem("1. Chế độ ăn lỏng", R.drawable.anlong2),
    NutritionItem("2. Chế độ ăn kiêng cho người tiểu đường", R.drawable.ankieng),
    NutritionItem("3. Chế độ dinh dưỡng giàu calo", R.drawable.giaucalo),
    NutritionItem("4. Chế độ ăn ít cholesterol", R.drawable.choles),
    NutritionItem("5. Chế độ ăn chay", R.drawable.anchay),
    NutritionItem("6. Chế độ ăn ít natri", R.drawable.natri),
    NutritionItem("7. Chế độ dinh dưỡng ít và giàu protein", R.drawable.protein),

)
