package com.example.fitness.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flag          // ← Icon cho Thử thách
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    object Workout : BottomNavItem("Trang chủ", Icons.Filled.Home, "home")
    object Nutrition : BottomNavItem("Trợ lý", Icons.Filled.SupportAgent, "coach")
    object Challenge : BottomNavItem("Thử thách", Icons.Filled.Flag, "challenge")
    object Running : BottomNavItem("Chạy", Icons.Filled.DirectionsRun, "running")// ← Sửa đúng thứ tự
    object Profile : BottomNavItem("Cá nhân", Icons.Filled.Person, "profile")
}