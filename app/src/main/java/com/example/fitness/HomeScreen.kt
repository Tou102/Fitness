package com.example.fitness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.fitness.ui.components.AgeCategoryCard

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Trang chủ",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val cardData = listOf(
                    CardData("Tuổi: 18-29", R.drawable.nho, "age18to29"),
                    CardData("Tuổi: 30-49", R.drawable.nguoi_lon, "age30to39"),
                    CardData("Tuổi: 50+", R.drawable.download, "age50plus"),
                    CardData("Bài tập A", R.drawable.ic_launcher_foreground, "workouta"),
                    CardData("Bài tập B", R.drawable.ic_launcher_foreground, "workoutb"),
                    CardData("Bài tập C", R.drawable.ic_launcher_foreground, "workoutc"),
                    CardData("Bài tập D", R.drawable.ic_launcher_foreground, "workoutd")
                )
                items(cardData) { card ->
                    AgeCategoryCard(
                        title = card.title,
                        imageResourceId = card.imageResourceId,
                        onClick = {
                            navController.navigate(card.route)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Quay về", color = Color.Blue)
                }
                Button(
                    onClick = { navController.navigate("profile") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Hồ sơ", color = Color.Blue)
                }
            }
        }
    }
}

data class CardData(val title: String, val imageResourceId: Int, val route: String)
