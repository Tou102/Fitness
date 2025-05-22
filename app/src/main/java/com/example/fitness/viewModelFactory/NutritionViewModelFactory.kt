package com.example.fitness.viewModelFactory

import NutritionDetailViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness.dao.NutritionDetailDao

class NutritionDetailViewModelFactory(
    private val dao: NutritionDetailDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NutritionDetailViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
