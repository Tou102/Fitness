package com.example.fitness.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem(val label: String, val icon: ImageVector, val route: String) {
    object Workout : BottomNavItem("Tập luyện", Icons.Filled.FitnessCenter, "home")
    object Nutrition : BottomNavItem("Trợ lý", Icons.Filled.AutoAwesome, "coach")
    object Running : BottomNavItem("Chạy", Icons.Filled.DirectionsRun, "running")
    object Profile : BottomNavItem("Cá nhân", Icons.Filled.Person, "profile")
}
