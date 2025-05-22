package com.example.fitness.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.fitness.db.AppDatabase
import com.example.fitness.repository.ExerciseRepository
import com.example.fitness.viewModel.ExerciseViewModel



class ExerciseViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            val repository = ExerciseRepository(db.exerciseDao())  // Tạo repository
            return ExerciseViewModel(repository) as T  // Truyền repository vào ViewModel
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

