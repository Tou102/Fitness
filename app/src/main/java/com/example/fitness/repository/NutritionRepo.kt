package com.example.fitness.repository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.fitness.repository.NutritionRepo

fun testApi() {
    viewModelScope.launch {
        val result = NutritionRepo.getNutritionInfo("pho bo 300g")
        println("Kết quả API: $result")
    }
}

