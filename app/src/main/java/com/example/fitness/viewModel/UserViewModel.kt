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

//     Chỉ giữ hàm này vì dùng username để load user
    fun loadUserByUsername(username: String) {
        viewModelScope.launch {
            _user.value = db.userDao().getUserByUsername(username)
        }
    }
    fun logout() {
        viewModelScope.launch {
            // Xoá user (hoặc token, hoặc clear local data...)
            _user.value = null
            // Gọi repository.clearUserData() nếu cần
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

            // Load lại user mới nhất từ DB để cập nhật UI
            val refreshedUser = db.userDao().getUserByUsername(currentUser.username)
            _user.value = refreshedUser
        }
    }


    suspend fun loginUser(username: String, password: String): User? {
        return db.userDao().getUser(username, password)
    }

//    fun updateUser(name: String, email: String, phone: String, dob: String) {
//        viewModelScope.launch {
//            val currentUser = _user.value ?: return@launch
//            val updatedUser = currentUser.copy(
//                name = name,
//                email = email,
//                phone = phone,
//                dob = dob
//            )
//            db.userDao().update(updatedUser)
//            _user.value = updatedUser
//        }
//    }
}

