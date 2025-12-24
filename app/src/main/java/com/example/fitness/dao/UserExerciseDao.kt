package com.example.fitness.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitness.entity.UserExercise

@Dao
interface UserExerciseDao {
    // 1. Lấy danh sách bài tập đã sửa của user này
    @Query("SELECT * FROM user_exercises WHERE username = :username")
    suspend fun getCustomExercisesByUser(username: String): List<UserExercise>

    // 2. Lưu (hoặc cập nhật) bài tập.
    // Logic: Nếu đã có thì cập nhật, chưa có thì thêm mới
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userExercise: UserExercise)

    // 3. Xóa bài tập custom (Dùng cho nút Đặt lại)
    @Query("DELETE FROM user_exercises WHERE username = :username AND exerciseId = :exerciseId")
    suspend fun resetExercise(username: String, exerciseId: Int)

    // 4. Tìm xem dòng đó đã tồn tại chưa (để update ID cho chuẩn)
    @Query("SELECT * FROM user_exercises WHERE username = :username AND exerciseId = :exerciseId LIMIT 1")
    suspend fun findExisting(username: String, exerciseId: Int): UserExercise?


    @Query("DELETE FROM user_exercises WHERE username = :username AND exerciseId = :exerciseId")
    suspend fun deleteUserExercise(username: String, exerciseId: Int)
}