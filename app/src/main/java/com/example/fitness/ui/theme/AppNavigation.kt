package com.example.fitness.ui.theme

import AnChayChiTiet
import AnKiengChiTiet
import AnLongChiTiet
import BmiScreen
import CaloChiTiet
import CholesterolChiTiet
import NatriChiTiet
import ProfileScreen
import ProteinThapChiTiet
import WorkoutDetailScreen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitness.LoginScreen
import com.example.fitness.NutritionScreen
import com.example.fitness.RegisterScreen
import com.example.fitness.WorkoutScreen
import com.example.fitness.db.AppDatabase
import com.example.fitness.viewModel.UserViewModel

import com.example.fitness.viewModelFactory.UserViewModelFactory

@Composable
fun AppNavigation(navController: NavHostController, context: Context) {
    val db = AppDatabase.getDatabase(context)
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(db))

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("bmi") {
            BmiScreen(navController = navController) // Màn hình Workout
        }
        composable("workout") {
            WorkoutScreen(navController = navController) // Màn hình Workout
        }
        composable("nutrition")
        { NutritionScreen(navController)
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