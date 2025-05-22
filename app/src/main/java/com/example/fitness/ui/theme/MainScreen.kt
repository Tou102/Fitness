package com.example.fitness.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitness.ui.theme.*

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Lấy thông tin màn hình hiện tại
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Danh sách route không cần hiện BottomNavigation
    val noBottomNavRoutes = listOf("login", "register","gioithieu")

    Scaffold(
        bottomBar = {
            if (currentRoute !in noBottomNavRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        // Truyền paddingValues vào AppNavigation để tránh nội dung bị che khuất
        AppNavigation(
            navController = navController,
            context = LocalContext.current,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Workout,
        BottomNavItem.Nutrition,
        BottomNavItem.BieuDo,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
