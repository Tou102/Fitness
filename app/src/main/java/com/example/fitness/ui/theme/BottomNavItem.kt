package com.example.fitness.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.extended.*

sealed class BottomNavItem(val label: String, val icon: ImageVector, val route: String) {
    object Workout : BottomNavItem("Trang chủ", Icons.Filled.Home, "workout")
    object Nutrition : BottomNavItem("Chế độ", Icons.Filled.Restaurant, "nutrition")
    object BieuDo : BottomNavItem("Biểu đồ", Icons.Filled.ShowChart, "bieudo")
    object Profile : BottomNavItem("Cá nhân", Icons.Filled.Person, "profile")
}
