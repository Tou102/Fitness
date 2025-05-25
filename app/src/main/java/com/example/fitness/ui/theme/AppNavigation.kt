
package com.example.fitness.ui.theme


import BmiScreen
import CaloriesScreen
import ProfileScreen
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

        composable("gioithieu") {
            FitnessIntroPager(navController = navController)
        }

        composable("anlong_detail") { AnLongChiTiet(navController, nutritionDetailViewModel) }
        composable("ankieng_detail") { AnKiengChiTiet(navController, nutritionDetailViewModel) }
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
    }
}
