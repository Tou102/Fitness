package com.example.fitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.example.fitness.ui.theme.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.fitness.entity.AppRepository.initialize(applicationContext)
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