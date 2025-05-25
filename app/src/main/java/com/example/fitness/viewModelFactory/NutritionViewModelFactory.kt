package com.example.fitness.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness.db.AppDatabase
import com.example.fitness.repository.NutritionDetailRepository
import com.example.fitness.viewModel.NutritionDetailViewModel

class NutritionDetailViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionDetailViewModel::class.java)) {
            val repository = NutritionDetailRepository(db.nutritionDetailDao())  // Tạo repository nutrition
            return NutritionDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
