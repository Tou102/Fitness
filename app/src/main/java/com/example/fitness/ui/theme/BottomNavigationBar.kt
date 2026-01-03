package com.example.fitness.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FitnessCenter  // ← thêm nếu dùng
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant     // ← thêm nếu dùng
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// BottomNavItem định nghĩa như trên

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    hasActiveChallenge: Boolean = false
) {
    val items = listOf(
        BottomNavItem.Workout,
        BottomNavItem.Nutrition,
        BottomNavItem.Running,
        BottomNavItem.Challenge,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.94f),
        tonalElevation = 2.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(16.dp, RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
    ) {
        items.forEach { item ->
            val isLocked = hasActiveChallenge && item !in listOf(
                BottomNavItem.Running,
                BottomNavItem.Challenge
            )

            val selected = currentRoute == item.route

            val iconTint by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.primary
                else if (isLocked) Color(0xFFBBBBBB)
                else Color(0xFF888888)
            )

            val textColor by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.primary
                else if (isLocked) Color(0xFFBBBBBB)
                else Color(0xFF888888)
            )

            NavigationBarItem(
                selected = selected,
                enabled = !isLocked,
                onClick = {
                    if (!isLocked && !selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
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
                        modifier = Modifier
                            .size(26.dp)
                            .alpha(if (isLocked) 0.45f else 1f)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = textColor,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 11.sp,
                        modifier = Modifier.alpha(if (isLocked) 0.45f else 1f)
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Transparent, // để animate tự xử lý
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Transparent
                )
            )
        }
    }
}