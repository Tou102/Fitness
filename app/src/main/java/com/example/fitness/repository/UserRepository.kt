//package com.example.fitness.repository
//
//import User
//import UserDao
//
//import kotlinx.coroutines.flow.Flow
//
//class UserRepository(private val userDao: UserDao) {
//
//    // Lấy tất cả người dùng từ database
//    fun getAllUsers(): Flow<List<User>> = userDao.getAllUser()
//
//    suspend fun getUserByUsernameAndPassword(username: String, password: String): User? {
//        return userDao.getUserByUsernameAndPassword(username, password)
//    }
//    // Thêm người dùng mới vào database
//    suspend fun addUser(user: User) {
//        userDao.addUser(user)
//    }
//
//    // Cập nhật thông tin người dùng trong database
//    suspend fun updateUser(user: User) {
//        userDao.updateUser(user)
//    }
//
//    // Xóa người dùng khỏi database
//    suspend fun deleteUser(user: User) {
//        userDao.deleteUser(user)
//    }
//
//}
