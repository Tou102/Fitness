package com.example.fitness.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_exercises")
data class UserExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,    // Của user nào
    val exerciseId: Int,     // Bài tập ID mấy (VD: Bài 1 - Bật nhảy)
    val customValue: Int,     // Giá trị đã sửa (VD: 50s)
    val activeExerciseId: Int = exerciseId
)