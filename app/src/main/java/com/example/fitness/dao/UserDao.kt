package com.example.fitness.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.fitness.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Các hàm truy vấn liên quan đến bảng User

    // Lấy tất cả người dùng
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>  // Sử dụng Flow cho bất đồng bộ

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): Flow<User?>

    // Lấy người dùng theo ID
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: Int): Flow<User?> // Sử dụng Flow và trả về Nullable nếu không tìm thấy

    // Lấy người dùng theo email
    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): Flow<User?>

    // Thêm người dùng mới
    @Insert
    suspend fun insertUser(user: User)  // suspend cho các hàm bất đồng bộ

    // Cập nhật thông tin người dùng
    @Update
    suspend fun updateUser(user: User)

    // Xóa người dùng
    @Delete
    suspend fun deleteUser(user: User)

    // Kiểm tra đăng nhập (chỉ trả về true/false, không trả về User)
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email AND password = :password)")
    suspend fun checkLogin(email: String, password: String): Boolean
}