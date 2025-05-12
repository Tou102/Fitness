package com.example.fitness.repository

import com.example.fitness.entity.User
import com.example.fitness.dao.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    // Lấy tất cả người dùng (trả về Flow)
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    // Lấy người dùng theo ID (trả về Flow)
    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }

    // Lấy người dùng theo email (trả về Flow)
    fun getUserByEmail(email: String): Flow<User?> {
        return userDao.getUserByEmail(email)
    }

    fun getUserByUsername(username: String): Flow<User?> {
        return userDao.getUserByUsername(username)
    }

    // Thêm người dùng mới (suspend function)
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    // Cập nhật thông tin người dùng (suspend function)
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    // Xóa người dùng (suspend function)
    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    // Kiểm tra đăng nhập (suspend function)
    suspend fun checkLogin(email: String, password: String): Boolean {
        return userDao.checkLogin(email, password)
    }
}