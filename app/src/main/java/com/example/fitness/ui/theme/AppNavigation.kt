package com.example.fitness.ui.theme

import CaloriesScreen
import ProfileScreen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitness.*
import com.example.fitness.db.AppDatabase
import com.example.fitness.repository.CaloriesRepository
import com.example.fitness.ui.screens.CoachAdviceScreen

import com.example.fitness.ui.screens.FitnessIntroPager
import com.example.fitness.ui.screens.NutritionScreen
import com.example.fitness.ui.screens.RunningTrackerScreen
import com.example.fitness.viewModel.CaloriesViewModel

import com.example.fitness.viewModel.ExerciseViewModel
import com.example.fitness.viewModel.NutritionDetailViewModel
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModel.WorkoutViewModel
import com.example.fitness.viewModelFactory.CaloriesViewModelFactory
import com.example.fitness.viewModelFactory.ExerciseViewModelFactory
import com.example.fitness.viewModelFactory.NutritionDetailViewModelFactory
import com.example.fitness.viewModelFactory.UserViewModelFactory
import com.example.fitness.viewModelFactory.WorkoutViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    modifier: Modifier = Modifier
) {
    val db = AppDatabase.getDatabase(context)

    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(db))
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory(db))
    val caloriesRepository = CaloriesRepository(db.caloriesRecordDao())
    val caloriesViewModel: CaloriesViewModel = viewModel(factory = CaloriesViewModelFactory(caloriesRepository))
    val nutritionDetailViewModel: NutritionDetailViewModel = viewModel(factory = NutritionDetailViewModelFactory(db))
    val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(db.workoutSessionDao()))

    val userState by userViewModel.user.collectAsState(initial = null)
    val currentUserId = userState?.id ?: 0
    val isAdmin = userState?.role == "admin"  // Giả sử User entity có trường role kiểu String

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
        composable("coach") {
            CoachAdviceScreen(navController = navController)
        }
        composable("workout") {
            WorkoutScreen(
                navController = navController,
                workoutViewModel = workoutViewModel,
                userId = currentUserId,
                isAdmin = isAdmin   // thêm tham số phân quyền
            )
        }

        composable("nutrition") {
            NutritionScreen(
                navController = navController
            )
        }


        composable(route = "running") {
            RunningTrackerScreen(
                navController = navController,                 // ✅ truyền vào
                caloriesViewModel = caloriesViewModel,         // ✅ truyền vào
                onNavigateToSave = { navController.navigate("calories") }
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
        composable("gioithieu") {
            FitnessIntroPager(navController = navController)
        }


        // Các route khác giữ nguyên
        composable("workoutDetails/FULLBODY") {
            FullBody(navController, exerciseViewModel, isAdmin)
        }
        composable("workoutDetails/ABS") {
            Abs(navController, exerciseViewModel, isAdmin)
        }
        composable("workoutDetails/CHEST") {
            Chest(navController, exerciseViewModel, isAdmin)
        }
        composable("workoutDetails/ARM") {
            Arm(navController, exerciseViewModel, isAdmin)
        }

        composable("water") {
            WaterIntakeScreen(navController = navController, db = db)
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                userViewModel = userViewModel,
                workoutViewModel = workoutViewModel,
                userId = currentUserId
            )
        }


        composable("exercise_camera/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            ExerciseCameraScreen(exerciseId = id)
        }
    }
}
