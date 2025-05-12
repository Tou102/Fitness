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
fun WorkoutScreen(navController: NavController) {
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
                    CardData("Tuổi: 18-29", R.drawable.nho, "ageDetails/18-29"), // giữ nguyên đường dẫn
                    CardData("Tuổi: 30-49", R.drawable.nguoi_lon, "ageDetails/30-49"), // giữ nguyên
                    CardData("Tuổi: 50+", R.drawable.download, "ageDetails/50+"), // giữ nguyên
                    CardData("Võ", R.drawable.tapvo, "workoutDetails/vo"), //thay đổi đường dẫn
                    CardData("Yoga", R.drawable.yoga, "workoutDetails/yoga"), //thay đổi đường dẫn

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

                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        navController.navigate("nutrition") {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Tiếp tục", color = Color.White)
                }
            }
        }
    }



data class CardData(val title: String, val imageResourceId: Int, val route: String)
