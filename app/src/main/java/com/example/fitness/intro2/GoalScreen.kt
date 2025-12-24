package com.example.fitness.intro2


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import com.example.fitness.style.*

@Composable
fun GoalScreen(navController: NavHostController) {
    var selectedGoal by remember { mutableStateOf("") }
    val goals = listOf("Giảm cân", "Xây dựng cơ bắp", "Giữ dáng")
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            ScreenHeader(title = "Mục tiêu chính của\nbạn là gì?")

            goals.forEach { item ->
                val isSelected = selectedGoal == item
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) BrandBlue.copy(
                            alpha = 0.1f
                        ) else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) BrandBlue else Color.LightGray.copy(0.4f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedGoal = item }
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandBlue else Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bấm Next -> Chuyển sang màn Body
            NextButton(onClick = { navController.navigate("cannang") })
        }
    }
