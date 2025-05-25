package com.example.fitness


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import com.example.fitness.ui.screens.MainScreen
import com.example.fitness.ui.theme.FitnessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen()



                }
            }
        }
    }
}

