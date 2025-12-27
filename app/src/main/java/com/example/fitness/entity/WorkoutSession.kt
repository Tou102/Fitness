package com.example.fitness.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int = 0,
    val userId: Int,
    val date: Long,  // Phải có trường này nếu dùng trong query
    val day: Int,    // Thường lưu ngày trong tuần (1..7)
    val workoutType: String,
    val completed: Boolean = false,
    val notes: String? = null
)

