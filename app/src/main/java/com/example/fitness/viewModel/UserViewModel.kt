package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.fitness.db.AppDatabase

import com.example.fitness.entity.User
import kotlinx.coroutines.launch

class UserViewModel(private val db: AppDatabase) : ViewModel() {

    // Đăng ký người dùng
    fun registerUser(username: String, password: String) {
        viewModelScope.launch {
            val user = User(username = username, password = password)
            db.userDao().insert(user)
        }
    }

    // Đăng nhập người dùng
    suspend fun loginUser(username: String, password: String): User? {
        return db.userDao().getUser(username, password)
    }
}
