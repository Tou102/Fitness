package com.example.fitness.ui.theme

import BieuDo
import BmiScreen
import NutritionDetailViewModel
import ProfileScreen
import WorkoutDetailScreen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitness.*

import com.example.fitness.db.AppDatabase
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModelFactory.UserViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    modifier: Modifier = Modifier
) {
    val db = AppDatabase.getDatabase(context)
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(db))

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

        composable("anlong_detail") { AnLongChiTiet(navController) }
        composable("ankieng_detail") { AnKiengChiTiet(navController) }
        composable("calo_detail") { CaloChiTiet(navController) }
        composable("choles_detail") { CholesterolChiTiet(navController) }
        composable("anchay_detail") { AnChayChiTiet(navController) }
        composable("natri_detail") { NatriChiTiet(navController) }
        composable("protein_detail") { ProteinThapChiTiet(navController) }

        composable("workoutDetails/{workoutType}") { backStackEntry ->
            val workoutType = backStackEntry.arguments?.getString("workoutType")
            WorkoutDetailScreen(workoutType = workoutType, navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}
