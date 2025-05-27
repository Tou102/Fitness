
package com.example.fitness.ui.theme

import BmiScreen


//import CaloChiTiet
//import CholesterolChiTiet
//import NatriChiTiet
//import ProteinThapChiTiet

import NutritionDetailViewModel
import ProfileScreen
//import WorkoutDetailScreen

import CaloriesDailySummaryScreen
import CaloriesScreen
import CaloriesScreenWithViewModel
import NutritionDetailViewModel
import ProfileScreen



import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


package com.example.fitness.ui.theme

import BieuDo
import BmiScreen
import ProfileScreen
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


import com.example.fitness.BaiTap18den29


import com.example.fitness.LoginScreen
import com.example.fitness.NutritionScreen
import com.example.fitness.RegisterScreen
import com.example.fitness.WorkoutScreen
import com.example.fitness.db.AppDatabase
import com.example.fitness.viewModel.ExerciseViewModel
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModelFactory.ExerciseViewModelFactory

import com.example.fitness.viewModelFactory.UserViewModelFactory
import com.example.fitness.viewModel.CaloriesViewModel
import com.example.fitness.viewModelFactory.CaloriesViewModelFactory
import com.example.fitness.*


import com.example.fitness.ui.screens.FitnessIntroPager
import com.example.fitness.viewModel.WaterIntakeViewModel


import com.example.fitness.*


import com.example.fitness.ui.screens.FitnessIntroPager
import com.example.fitness.viewModel.NutritionDetailViewModel
import com.example.fitness.viewModelFactory.NutritionDetailViewModelFactory




import com.example.fitness.viewModelFactory.UserViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    modifier: Modifier = Modifier
) {
    val db = AppDatabase.getDatabase(context)
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(db))

    val exerciseViewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory(db))
    val caloriesRepository = remember { com.example.fitness.repository.CaloriesRepository(db.caloriesRecordDao()) }
    val caloriesViewModel: CaloriesViewModel = viewModel(factory = CaloriesViewModelFactory(caloriesRepository))

    val factory = ExerciseViewModelFactory(db)
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = factory)

    val nutritionFactory = NutritionDetailViewModelFactory(db)
    val nutritionDetailViewModel: NutritionDetailViewModel = viewModel(factory = nutritionFactory)

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
        composable("bieudo") {
            BieuDo(navController = navController)
        }
        composable("gioithieu") {
            FitnessIntroPager(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }

        composable("anlong_detail") { AnLongChiTiet(navController, nutritionDetailViewModel) }
        composable("ankieng_detail") { AnKiengChiTiet(navController,nutritionDetailViewModel) }
        composable("calo_detail") { CaloChiTiet(navController, nutritionDetailViewModel) }
        composable("choles_detail") { CholesterolChiTiet(navController, nutritionDetailViewModel) }
        composable("anchay_detail") { AnChayChiTiet(navController, nutritionDetailViewModel) }
        composable("natri_detail") { NatriChiTiet(navController, nutritionDetailViewModel) }
        composable("protein_detail") { ProteinThapChiTiet(navController, nutritionDetailViewModel) }

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

 
    @Composable
    fun AppNavigation(
        navController: NavHostController,
        context: Context,
        modifier: Modifier = Modifier

    ) {
        // Các route của màn hình khác

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
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("calories_daily_summary") {
            CaloriesDailySummaryScreen(
                records = caloriesViewModel.records.collectAsState().value,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        // Thêm màn hình WaterIntakeScreen với route khác tránh trùng với "profile"
        composable("water") {
            // Truyền thêm tham số db vào màn hình WaterIntakeScreen
            WaterIntakeScreen(navController = navController, db = db)
        }

        // Các màn chi tiết khác
        composable("anlong_detail") { AnLongChiTiet(navController) }
        composable("ankieng_detail") { AnKiengChiTiet(navController) }
        composable("calo_detail") { CaloChiTiet(navController) }
        composable("choles_detail") { CholesterolChiTiet(navController) }
        composable("anchay_detail") { AnChayChiTiet(navController) }
        composable("natri_detail") { NatriChiTiet(navController) }
        composable("protein_detail") { ProteinThapChiTiet(navController) }
        composable("workoutDetails/18-29") {
            BaiTap18den29(navController, exerciseViewModel)
        }
    }
}
