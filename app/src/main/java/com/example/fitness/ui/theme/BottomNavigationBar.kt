package com.example.fitness.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Workout,
        BottomNavItem.Nutrition,
        BottomNavItem.Running,
        BottomNavItem.Profile
    )

    // Nhận route hiện tại từ NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 8.dp,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        items.forEach { item ->
            // Kiểm tra mục nào được chọn
            val selected = currentRoute == item.route

            // Màu sắc của biểu tượng và text khi được chọn
            val iconTint by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
            )

            // Tạo item cho BottomNavigation
            NavigationBarItem(
                selected = selected,
                onClick = {
                    // Điều hướng chỉ khi chưa chọn
                    if (!selected) {
                        navController.navigate(item.route) {
                            // Pop các destination trước đó và giữ trạng thái màn hình
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = textColor,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                },
                alwaysShowLabel = true, // Luôn hiển thị label
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
