package com.example.fitness

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.example.fitness.ui.navigation.AppNavigation  // sửa package nếu cần

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    context = this@MainActivity
                )
            }
        }
    }
}