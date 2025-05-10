package com.example.fitness.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitness.HomeScreen
import com.example.fitness.ProfileScreen
import com.example.fitness.AgeDetailsScreen
import com.example.fitness.WorkoutDetailsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("age18to29") { AgeDetailsScreen(navController, "18-29") }
        composable("age30to39") { AgeDetailsScreen(navController, "30-39") }
        composable("age40to49") { AgeDetailsScreen(navController, "40-49") }
        composable("age50plus") { AgeDetailsScreen(navController, "50+") }
        composable("workouta") { WorkoutDetailsScreen(navController, "Workout A") }
        composable("workoutb") { WorkoutDetailsScreen(navController, "Workout B") }
        composable("workoutc") { WorkoutDetailsScreen(navController, "Workout C") }
        composable("workoutd") { WorkoutDetailsScreen(navController, "Workout D") }
    }
}
