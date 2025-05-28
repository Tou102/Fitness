package com.example.fitness.ui.theme

import CaloriesScreen
import ProfileScreen
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitness.*
import com.example.fitness.db.AppDatabase
import com.example.fitness.ui.screens.FitnessIntroPager
import com.example.fitness.viewModel.CaloriesViewModel
import com.example.fitness.viewModel.ExerciseViewModel
import com.example.fitness.viewModel.NutritionDetailViewModel
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModel.WaterIntakeViewModel
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
    // Database
    val db = AppDatabase.getDatabase(context)

    // ViewModels khởi tạo 1 lần duy nhất bằng remember hoặc viewModel
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(db))
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory(db))
    val caloriesRepository = com.example.fitness.repository.CaloriesRepository(db.caloriesRecordDao())
    val caloriesViewModel: CaloriesViewModel = viewModel(factory = CaloriesViewModelFactory(caloriesRepository))
    val nutritionDetailViewModel: NutritionDetailViewModel = viewModel(factory = NutritionDetailViewModelFactory(db))
    val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(db.workoutSessionDao()))

    // Lấy userId an toàn, mặc định 0 nếu chưa có user
    val userId by userViewModel.user.collectAsState(initial = null)
    val currentUserId = userId?.id ?: 0

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
            // Truyền workoutViewModel và userId đã lấy
            WorkoutScreen(
                navController = navController,
                workoutViewModel = workoutViewModel,
                userId = currentUserId
            )

        }
        composable("nutrition") {
            NutritionScreen(navController = navController)
        }
        composable("running") {
            RunningTrackerScreen(
                onNavigateToSave = { navController.navigate("calories") },
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
        composable("gioithieu") {
            FitnessIntroPager(navController = navController)
        }
        composable("anlong_detail") {
            AnLongChiTiet(navController, nutritionDetailViewModel)
        }
        composable("ankieng_detail") {
            AnKiengChiTiet(navController, nutritionDetailViewModel)
        }
        composable("calo_detail") {
            CaloChiTiet(navController, nutritionDetailViewModel)
        }
        composable("choles_detail") {
            CholesterolChiTiet(navController, nutritionDetailViewModel)
        }
        composable("anchay_detail") {
            AnChayChiTiet(navController, nutritionDetailViewModel)
        }
        composable("natri_detail") {
            NatriChiTiet(navController, nutritionDetailViewModel)
        }
        composable("protein_detail") {
            ProteinThapChiTiet(navController, nutritionDetailViewModel)
        }
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
