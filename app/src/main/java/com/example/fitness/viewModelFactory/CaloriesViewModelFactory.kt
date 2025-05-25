package com.example.fitness.viewModelFactory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness.repository.CaloriesRepository
import com.example.fitness.viewModel.CaloriesViewModel


class CaloriesViewModelFactory(private val repository: CaloriesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CaloriesViewModel(repository) as T
    }
}

