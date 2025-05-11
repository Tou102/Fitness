package com.example.fitness

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("nutrition") { NutritionScreen(navController) }
        composable("water_detail") { WaterDetailScreen(navController) }
        composable("vegetable_detail") { VegetableDetailScreen(navController) }
        // Added the workout details routes
        composable(
            "ageDetails/{ageRange}",
            arguments = listOf(navArgument("ageRange") { type = NavType.StringType })
        ) { backStackEntry ->
            val ageRange = backStackEntry.arguments?.getString("ageRange")
            AgeDetailsScreen(ageRange = ageRange, navController = navController) // Pass navController
        }
        composable("workoutDetails/{workoutType}") { backStackEntry ->  // Thêm route mới
            val workoutType = backStackEntry.arguments?.getString("workoutType")
            WorkoutDetailScreen(workoutType = workoutType, navController = navController) // Gọi Composable mới và truyền navController
        }
    }
}

@Composable
fun AgeDetailsScreen(ageRange: String?, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween, // Pushes elements to top and bottom
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen content for Age Details
        Text(text = "Age Details for: $ageRange", style = MaterialTheme.typography.headlineMedium)
        // Back button
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Quay về")
        }
    }
}


@Composable
fun WorkoutDetailScreen(workoutType: String?, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween, // Pushes elements to top and bottom
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen content for Workout Details
        Text(text = "Workout Details for: $workoutType", style = MaterialTheme.typography.headlineMedium)
        // Back button
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Quay về")
        }
    }
}
