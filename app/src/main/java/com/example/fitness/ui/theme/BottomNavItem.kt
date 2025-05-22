package com.example.fitness.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val label: String, val icon: ImageVector, val route: String) {
    object Workout : BottomNavItem("Trang chủ", Icons.Filled.Home, "workout")
    object Nutrition : BottomNavItem("Chế độ", Icons.Filled.Menu, "nutrition")
    object BieuDo : BottomNavItem("Biểu đồ", Icons.Filled.CheckCircle, "bieudo")
    object Profile : BottomNavItem("Cá nhân", Icons.Filled.Person, "profile")
}
