package com.example.fitness.ui.theme

import BmiScreen
import CaloriesDailySummaryScreen
import CaloriesScreen
import CaloriesScreenWithViewModel

import ProfileScreen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.fitness.LoginScreen
import com.example.fitness.NutritionScreen
import com.example.fitness.RegisterScreen
import com.example.fitness.WorkoutScreen
import com.example.fitness.db.AppDatabase
import com.example.fitness.viewModel.ExerciseViewModel
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModelFactory.ExerciseViewModelFactory

import com.example.fitness.viewModel.CaloriesViewModel
import com.example.fitness.viewModelFactory.CaloriesViewModelFactory
import com.example.fitness.*

import com.example.fitness.ui.screens.FitnessIntroPager
import com.example.fitness.viewModel.NutritionDetailViewModel
import com.example.fitness.viewModel.WaterIntakeViewModel

import com.example.fitness.viewModelFactory.NutritionDetailViewModelFactory
import com.example.fitness.viewModelFactory.UserViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    modifier: Modifier = Modifier
) {
    // Get the database instance
    val db = AppDatabase.getDatabase(context)

    // Initialize ViewModels
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(db))
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory(db))
    val caloriesRepository = remember { com.example.fitness.repository.CaloriesRepository(db.caloriesRecordDao()) }
    val caloriesViewModel: CaloriesViewModel = viewModel(factory = CaloriesViewModelFactory(caloriesRepository))

    // Nutrition ViewModel
    val nutritionFactory = NutritionDetailViewModelFactory(db)
    val nutritionDetailViewModel: NutritionDetailViewModel = viewModel(factory = nutritionFactory)

    // Set up the Navigation Host
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("bmi") {
            BmiScreen(navController = navController)
        }
        composable("workout") {
            WorkoutScreen(navController = navController)
        }
        composable("nutrition") {
            NutritionScreen(navController = navController)
        }
        composable("running") {
            RunningTrackerScreen(
                onNavigateToSave = {
                    navController.navigate("calories")
                },
                caloriesViewModel = caloriesViewModel
            )
        }
        composable("calories") {
            CaloriesScreen(
                navController = navController,
                caloriesViewModel = caloriesViewModel
            )
        }
        composable("gioithieu") {
            FitnessIntroPager(navController = navController)
        }

        // Detail screens with Nutrition Detail ViewModel
        composable("anlong_detail") { AnLongChiTiet(navController, nutritionDetailViewModel) }
        composable("ankieng_detail") { AnKiengChiTiet(navController, nutritionDetailViewModel) }
        composable("calo_detail") { CaloChiTiet(navController, nutritionDetailViewModel) }
        composable("choles_detail") { CholesterolChiTiet(navController, nutritionDetailViewModel) }
        composable("anchay_detail") { AnChayChiTiet(navController, nutritionDetailViewModel) }
        composable("natri_detail") { NatriChiTiet(navController, nutritionDetailViewModel) }
        composable("protein_detail") { ProteinThapChiTiet(navController, nutritionDetailViewModel) }

        // Workout screens with Exercise ViewModel
        composable("workoutDetails/FULLBODY") {
            FullBody(navController, exerciseViewModel)
        }
        composable("workoutDetails/ABS") {
            Abs(navController, exerciseViewModel)
        }
        composable("workoutDetails/CHEST") {
            Chest(navController, exerciseViewModel)
        }
        composable("workoutDetails/ARM") {
            Arm(navController, exerciseViewModel)
        }

        // Calories screens
        composable("running") {
            RunningTrackerScreen(
                onNavigateToSave = {
                    navController.navigate("calories")
                },
                caloriesViewModel = caloriesViewModel
            )
        }
        composable("calories") {
            CaloriesScreen(
                navController = navController,
                caloriesViewModel = caloriesViewModel
            )
        }
        composable("calories_daily_summary") {
            CaloriesDailySummaryScreen(
                records = caloriesViewModel.records.collectAsState().value,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        // Water Intake screen
        composable("water") {
            WaterIntakeScreen(navController = navController, db = db)
        }
        composable("profile") {
            ProfileScreen(navController = navController,userViewModel = userViewModel)
        }
        composable("exercise_camera/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            ExerciseCameraScreen(exerciseId = id)
        }

        // Other screens (details)

    }
}