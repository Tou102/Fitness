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
            horizontalAlignment = Alignment.CenterHorizontally // Center the content horizontally
        ) {
            Text(
                text = "Bài tập",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally), // Center the grid
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val cardData = listOf(
                    CardData("Tuổi: 18-29", R.drawable.nhocc, "AgeDetailsScreen/18-29"),
                    CardData("Tuổi: 30-49", R.drawable.lon1, "AgeDetailsScreen/30-49"),
                    CardData("Tuổi: 50+", R.drawable.onggia, "AgeDetailsScreen/50+"),
                    CardData("Võ", R.drawable.vo, "AgeDetailsScreen"),
                    CardData("Yoga", R.drawable.yoga2, "AgeDetailsScreen"),

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

