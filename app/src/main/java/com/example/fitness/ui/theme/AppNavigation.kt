package com.example.fitness.ui.navigation

import CaloriesScreen
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.fitness.BmiScreen
import com.example.fitness.CaloriesDailySummaryScreen
import com.example.fitness.ExerciseEditScreen
import com.example.fitness.LoginScreen
import com.example.fitness.MainWorkoutScreen
import com.example.fitness.MiniGameScreen
import com.example.fitness.PlanDetailScreen
import com.example.fitness.QuizHomeScreen
import com.example.fitness.QuizPlayScreen
import com.example.fitness.RegisterScreen
import com.example.fitness.WorkoutSessionScreen
import com.example.fitness.WorkoutType
import com.example.fitness.camera.AIWorkoutScreen
import com.example.fitness.camera.DifficultyLevel
import com.example.fitness.camera.LevelScreen
import com.example.fitness.data.ChallengeKeys
import com.example.fitness.data.challengeDataStore
import com.example.fitness.db.AppDatabase
import com.example.fitness.intro2.BodyFocusScreen
import com.example.fitness.intro2.ChonCanNang
import com.example.fitness.intro2.GoalScreen
import com.example.fitness.repository.CaloriesRepository
import com.example.fitness.ui.screens.*
import com.example.fitness.ui.theme.BottomNavigationBar
import com.example.fitness.viewModel.*
import com.example.fitness.viewModelFactory.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map

@RequiresApi(Build.VERSION_CODES.O)
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
    val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(db.workoutSessionDao()))
    val challengeViewModel: ChallengeViewModel = viewModel(factory = ChallengeViewModel.factory(db.challengeDao()))

    val userState by userViewModel.user.collectAsStateWithLifecycle(initialValue = null)
    val currentUserId = userState?.id ?: 0

    var isChallengeLoading by remember { mutableStateOf(true) }
    val activeChallenge by challengeViewModel.activeChallenge.collectAsStateWithLifecycle(initialValue = null)

    // Fallback từ DataStore để khóa bottom bar ngay lập tức (ngăn flash mở khóa sau restart)
    val isLockedFromPrefs by context.challengeDataStore.data
        .map { preferences -> preferences[ChallengeKeys.IS_CHALLENGE_ACTIVE] ?: false }
        .collectAsStateWithLifecycle(initialValue = false)

    val hasActiveChallenge = activeChallenge != null || isLockedFromPrefs

    // Reload challenge NGAY KHI APP KHỞI ĐỘNG (sau kill/restart vẫn load sớm)
    LaunchedEffect(Unit) {
        challengeViewModel.reloadActiveChallenge()
        delay(300) // Đợi nhỏ để đảm bảo load xong trước khi UI recompose
        isChallengeLoading = false
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val noBottomBarRoutes = listOf(
        "login", "register", "gioithieu", "intro2", "muctieu", "cannang",
        "bmi", "ai_workout/{levelName}", "ai_level_select",
        "workout_session/{planId}", "history"
    )

    Scaffold(
        bottomBar = {
            if (currentRoute !in noBottomBarRoutes) {
                BottomNavigationBar(
                    navController = navController,
                    hasActiveChallenge = hasActiveChallenge
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Authentication & Onboarding
            composable("login") { LoginScreen(navController, userViewModel) }
            composable("register") { RegisterScreen(navController, userViewModel) }
            composable("gioithieu") { FitnessIntroPager(navController) }
            composable("intro2") { BodyFocusScreen(navController) }
            composable("muctieu") { GoalScreen(navController) }
            composable("cannang") { ChonCanNang(navController) }
            composable("bmi") { BmiScreen(navController) }

            // Main Home & Workout
            composable("home") {
                MainWorkoutScreen(
                    navController = navController,
                    onCameraClick = { navController.navigate("ai_level_select") }
                )
            }

            composable("ai_level_select") {
                LevelScreen(
                    onLevelSelected = { level ->
                        navController.navigate("ai_workout/${level.name}")
                    }
                )
            }

            composable(
                "ai_workout/{levelName}",
                arguments = listOf(navArgument("levelName") { type = NavType.StringType })
            ) { backStackEntry ->
                val levelName = backStackEntry.arguments?.getString("levelName") ?: "EASY"
                val level = DifficultyLevel.values().find { it.name == levelName } ?: DifficultyLevel.EASY
                AIWorkoutScreen(navController = navController, level = level)
            }

            // Plan & Exercise
            composable(
                "plan_detail/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.IntType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId") ?: 0
                PlanDetailScreen(
                    planId = planId,
                    onBack = { navController.popBackStack() },
                    onExerciseClick = { exId -> navController.navigate("exercise_edit/$exId") },
                    onStartWorkout = { navController.navigate("workout_session/$planId") }
                )
            }

            composable(
                "exercise_edit/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: 0
                ExerciseEditScreen(exerciseId = exerciseId, onBack = { navController.popBackStack() })
            }

            composable(
                "workout_session/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.IntType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId") ?: 0
                val startTime = androidx.compose.runtime.saveable.rememberSaveable { System.currentTimeMillis() }

                val workoutName = when (planId) {
                    1 -> "Bụng Người bắt đầu"
                    2 -> "Bụng Trung bình"
                    3 -> "Bụng Nâng cao"
                    4 -> "Cánh tay"
                    else -> "Bài tập $planId"
                }

                WorkoutSessionScreen(
                    planId = planId,
                    onExit = {
                        val duration = System.currentTimeMillis() - startTime
                        workoutViewModel.markTodayCompleted(currentUserId, workoutName, duration)
                        navController.popBackStack()
                    }
                )
            }

            // Challenge & Running
            composable("challenge") { ChallengeScreen(navController) }

            composable("running") {
                RunningTrackerScreen(
                    navController = navController,
                    caloriesViewModel = caloriesViewModel,
                    challengeViewModel = challengeViewModel,
                    onNavigateToSave = { navController.navigate("calories") },
                    isChallengeLoading = isChallengeLoading  // ← Truyền loading state
                )
            }

            // Calories & History
            composable("calories") { CaloriesScreen(navController, caloriesViewModel) }

            composable("calories_daily_summary") {
                CaloriesDailySummaryScreen(
                    records = caloriesViewModel.records.collectAsStateWithLifecycle().value,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("history") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    HistoryScreen(
                        navController = navController,
                        workoutViewModel = workoutViewModel,
                        userId = currentUserId
                    )
                } else {
                    Text("Yêu cầu Android 8.0 trở lên để xem lịch sử")
                }
            }

            // Coach / Chat
            composable("coach?runSummary={runSummary}") { entry ->
                val summary = entry.arguments?.getString("runSummary")?.replace("+", " ") ?: ""
                val chatViewModel: ChatViewModel = viewModel()

                LaunchedEffect(summary) {
                    if (summary.isNotBlank()) {
                        chatViewModel.sendMessage("Kết quả chạy của tôi:\n$summary")
                    }
                }
                ChatScreen()
            }

            // Other Features
            composable("leaderboard") { LeaderboardScreen(navController) }
            composable("minigame") { MiniGameScreen(navController, db) }
            composable("quiz_home") { QuizHomeScreen(navController) }

            composable(
                "quiz_play/{workout}/{level}",
                arguments = listOf(
                    navArgument("workout") { type = NavType.StringType },
                    navArgument("level") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val workoutName = backStackEntry.arguments?.getString("workout") ?: WorkoutType.FULLBODY.name
                val level = backStackEntry.arguments?.getInt("level") ?: 1
                QuizPlayScreen(navController, WorkoutType.valueOf(workoutName), level)
            }

            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    userViewModel = userViewModel,
                    workoutViewModel = workoutViewModel,
                    userId = currentUserId
                )
            }

            composable(
                "user_detail/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                UserDetailScreen(navController, userId)
            }
        }
    }
}