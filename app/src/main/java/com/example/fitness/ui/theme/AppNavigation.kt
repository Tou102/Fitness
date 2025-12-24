package com.example.fitness.ui.theme

import CaloriesScreen
import ProfileScreen
import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitness.Abs
import com.example.fitness.Arm
import com.example.fitness.BmiScreen
import com.example.fitness.CaloriesDailySummaryScreen
import com.example.fitness.Chest
import com.example.fitness.FullBody
import com.example.fitness.LoginScreen
import com.example.fitness.RegisterScreen
import com.example.fitness.MiniGameScreen
import com.example.fitness.QuizHomeScreen
import com.example.fitness.QuizPlayScreen
import com.example.fitness.WorkoutScreen
import com.example.fitness.WorkoutType
import com.example.fitness.db.AppDatabase
import com.example.fitness.repository.CaloriesRepository
import com.example.fitness.ui.screens.ChatScreen
import com.example.fitness.ui.screens.FitnessIntroPager
import com.example.fitness.ui.screens.RunningTrackerScreen
import com.example.fitness.viewModel.CaloriesViewModel
import com.example.fitness.viewModel.ChatViewModel
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
    val isAdmin = userState?.role == "admin"

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val noBottom = listOf("login", "register", "gioithieu")

    Scaffold(
        bottomBar = {
            if (currentRoute !in noBottom) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "profile",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(navController = navController, userViewModel = userViewModel)
            }
            composable("register") {
                RegisterScreen(navController = navController, userViewModel = userViewModel)
            }
            composable("gioithieu") {
                FitnessIntroPager(navController = navController)
            }
            composable("bmi") {
                BmiScreen(navController = navController)
            }
            composable("workout") {
                WorkoutScreen(
                    navController = navController,
                    workoutViewModel = workoutViewModel,
                    userId = currentUserId,
                    isAdmin = isAdmin
                )
            }

            // NHẬN DỮ LIỆU TỪ RUNNING → GỬI VÀO CHAT
            composable("coach?runSummary={runSummary}") { entry ->
                val summary = entry.arguments?.getString("runSummary") ?: ""
                val chatViewModel: ChatViewModel = viewModel()   // Lấy đúng instance

                LaunchedEffect(summary) {
                    if (summary.isNotBlank()) {
                        chatViewModel.sendMessage("Kết quả chạy của tôi:\n$summary")
                    }
                }
                ChatScreen()
            }

            composable("running") {
                RunningTrackerScreen(
                    navController = navController,
                    caloriesViewModel = caloriesViewModel,
                    onNavigateToSave = { navController.navigate("calories") }
                )
            }
            composable("calories") {
                CaloriesScreen(navController = navController, caloriesViewModel = caloriesViewModel)
            }
            composable("calories_daily_summary") {
                CaloriesDailySummaryScreen(
                    records = caloriesViewModel.records.collectAsState().value,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
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
            composable("minigame") {
                MiniGameScreen(navController = navController, db = db)
            }
            composable(route = "quiz_home") {
                QuizHomeScreen(navController = navController)
            }
            composable(
                route = "quiz_play/{workout}/{level}"
            ) { backStackEntry ->

                val workoutName = backStackEntry.arguments?.getString("workout")
                    ?: WorkoutType.FULLBODY.name
                val workout = WorkoutType.valueOf(workoutName)

                val level = backStackEntry.arguments
                    ?.getString("level")
                    ?.toIntOrNull() ?: 1

                QuizPlayScreen(
                    navController = navController,
                    workout = workout,
                    level = level
                )
            }


            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    userViewModel = userViewModel,
                    workoutViewModel = workoutViewModel,
                    userId = currentUserId
                )
            }
        }
    }
}