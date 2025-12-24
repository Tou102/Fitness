package com.example.fitness.ui.theme

import CaloriesScreen

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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.fitness.Abs
import com.example.fitness.Arm
import com.example.fitness.BmiScreen
import com.example.fitness.CaloriesDailySummaryScreen

import com.example.fitness.ExerciseEditScreen

import com.example.fitness.LoginScreen
import com.example.fitness.MainWorkoutScreen
import com.example.fitness.PlanDetailScreen
import com.example.fitness.RegisterScreen

import com.example.fitness.WaterIntakeScreen

import com.example.fitness.WorkoutSessionScreen

import com.example.fitness.MiniGameScreen
import com.example.fitness.QuizHomeScreen
import com.example.fitness.QuizPlayScreen
import com.example.fitness.WorkoutScreen
import com.example.fitness.WorkoutType

import com.example.fitness.db.AppDatabase
import com.example.fitness.intro2.BodyFocusScreen
import com.example.fitness.intro2.ChonCanNang
import com.example.fitness.intro2.GoalScreen
import com.example.fitness.repository.CaloriesRepository
import com.example.fitness.ui.screens.ChatScreen
import com.example.fitness.ui.screens.FitnessIntroPager
import com.example.fitness.ui.screens.ProfileScreen
import com.example.fitness.ui.screens.RunningTrackerScreen
import com.example.fitness.viewModel.CaloriesViewModel
import com.example.fitness.viewModel.ChatViewModel

import com.example.fitness.viewModel.NutritionDetailViewModel
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModel.WorkoutViewModel
import com.example.fitness.viewModelFactory.CaloriesViewModelFactory
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

    val caloriesRepository = CaloriesRepository(db.caloriesRecordDao())
    val caloriesViewModel: CaloriesViewModel = viewModel(factory = CaloriesViewModelFactory(caloriesRepository))
    // val nutritionDetailViewModel: NutritionDetailViewModel = viewModel(factory = NutritionDetailViewModelFactory(db))
    val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(db.workoutSessionDao()))

    val userState by userViewModel.user.collectAsState(initial = null)
    val currentUserId = userState?.id ?: 0
    val isAdmin = userState?.role == "admin"

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val noBottomBarRoutes = listOf("login", "register", "gioithieu","intro2","muctieu","cannang", "workout_session/{planId}")



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

            composable("login") { LoginScreen(navController, userViewModel) }
            composable("register") { RegisterScreen(navController, userViewModel) }
            composable("gioithieu") { FitnessIntroPager(navController) }
            composable("bmi") { BmiScreen(navController) }
            composable("intro2") { BodyFocusScreen(navController) }
            composable("muctieu") { GoalScreen(navController) }
            composable("cannang") { ChonCanNang(navController) }

//            composable("workout") {
//                WorkoutScreenRoute(navController = navController, userId = currentUserId, isAdmin = isAdmin)
//            }
            composable("home"){ MainWorkoutScreen(navController)}
            // 2. Màn hình danh sách bài tập (Khi bấm vào gói tập)
            composable(
                route = "plan_detail/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.IntType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId") ?: 0
                PlanDetailScreen(
                    planId = planId,
                    onBack = { navController.popBackStack() },
                    onExerciseClick = { exerciseId ->
                        navController.navigate("exercise_edit/$exerciseId")
                    },
                    onStartWorkout = {
                        navController.navigate("workout_session/$planId")
                    }
                )
            }

            // 3. Màn hình sửa bài tập (Khi bấm vào bài tập)
            composable(
                route = "exercise_edit/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: 0
                ExerciseEditScreen(
                    exerciseId = exerciseId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "workout_session/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.IntType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId") ?: 0

                // Gọi màn hình tập luyện -> Lúc này hàm WorkoutSessionScreen sẽ chuyển màu XANH
                WorkoutSessionScreen(
                    planId = planId,
                    onExit = {
                        navController.popBackStack() // Quay về danh sách khi tập xong
                    }

    
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


            // SỬA HOÀN CHỈNH – KHÔNG CÒN LỖI ĐỎ NỮA
            val workoutTypes = listOf("FULLBODY", "ABS", "CHEST", "ARM")
//            workoutTypes.forEach { type ->
//                composable(
//                    route = "workoutDetails/$type?isAdmin={isAdmin}",
//                    arguments = listOf(navArgument("isAdmin") { defaultValue = "false" })
//                ) { entry ->
//                    val isAdmin = entry.arguments?.getString("isAdmin")?.toBoolean() ?: false
//
//                    when (type) {
//                        "FULLBODY" -> FullBody(navController, exerciseViewModel, isAdmin)
//                        "ABS"      -> Abs(navController, exerciseViewModel, isAdmin)
//                        "CHEST"    -> Chest(navController, exerciseViewModel, isAdmin)
//                        "ARM"      -> Arm(navController, exerciseViewModel, isAdmin)
//                    }
//                }
//            }


        }
    }
}