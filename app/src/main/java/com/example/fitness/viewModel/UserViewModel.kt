package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.fitness.db.AppDatabase

import com.example.fitness.entity.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class UserViewModel(private val db: AppDatabase) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun loadUserByUsername(username: String) {
        viewModelScope.launch {
            _user.value = db.userDao().getUserByUsername(username)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _user.value = null
        }
    }

    fun registerUser(username: String, password: String) {
        viewModelScope.launch {
            val user = User(username = username, password = password)
            db.userDao().insert(user)
        }
    }

    fun updateProfile(nickname: String?, avatarUriString: String?) {
        viewModelScope.launch {
            val currentUser = _user.value ?: return@launch
            val updatedUser = currentUser.copy(nickname = nickname, avatarUriString = avatarUriString)
            db.userDao().update(updatedUser)

            val refreshedUser = db.userDao().getUserByUsername(currentUser.username)
            _user.value = refreshedUser
        }
    }

    suspend fun loginUser(username: String, password: String): User? {
        return db.userDao().getUser(username, password)
    }

    // Thêm hàm kiểm tra quyền admin
    fun isAdmin(): Boolean {
        return _user.value?.role == "admin"
    }
}

