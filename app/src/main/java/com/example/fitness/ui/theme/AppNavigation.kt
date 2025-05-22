package com.example.fitness.ui.theme

import BieuDo
import BmiScreen

import CaloChiTiet
import CholesterolChiTiet
import NatriChiTiet
import ProteinThapChiTiet

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

import com.example.fitness.BaiTap18den29

import com.example.fitness.LoginScreen
import com.example.fitness.NutritionScreen
import com.example.fitness.RegisterScreen
import com.example.fitness.WorkoutScreen
import com.example.fitness.db.AppDatabase
import com.example.fitness.viewModel.ExerciseViewModel
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModelFactory.ExerciseViewModelFactory

import com.example.fitness.*

import com.example.fitness.db.AppDatabase
import com.example.fitness.ui.screens.FitnessIntroPager


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
    val factory = ExerciseViewModelFactory(db)
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = factory)

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



        composable("profile") {
            ProfileScreen(navController = navController)
        }

    }
}
