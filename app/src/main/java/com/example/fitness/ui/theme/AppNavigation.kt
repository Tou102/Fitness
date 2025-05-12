package com.example.fitness

import AnChayChiTiet
import AnKiengChiTiet
import AnLongChiTiet
import BmiScreen
import CaloChiTiet
import CholesterolChiTiet
import NatriChiTiet
import BmiScreen
import ProteinThapChiTiet
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
import com.example.fitness.LoginScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("workout") { WorkoutScreen(navController) }
        composable("nutrition") { NutritionScreen(navController) }
        composable("anlong_detail") { AnLongChiTiet(navController) }
        composable("ankieng_detail") { AnKiengChiTiet(navController) }
        composable("calo_detail") { CaloChiTiet(navController) }
        composable("choles_detail") { CholesterolChiTiet(navController) }
        composable("anchay_detail") { AnChayChiTiet(navController) }
        composable("natri_detail") { NatriChiTiet(navController) }
        composable("protein_detail") { ProteinThapChiTiet(navController) }

        composable("bmi") {BmiScreen (navController) }

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
fun ChiTietAnLong(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween, // Pushes elements to top and bottom
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen content for Age Details

        // Back button
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Quay về")
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
        Text(text = "Độ tuổi từ : $ageRange", style = MaterialTheme.typography.headlineMedium)
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
