package com.example.fitness.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness.db.AppDatabase
import com.example.fitness.viewModel.UserViewModel

class UserViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
