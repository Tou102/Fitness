package com.example.fitness.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: String,
    val dayNumber: Int,
    val targetDistanceMeters: Float = 0f,
    val targetTimeSeconds: Long = 0L,
    val description: String,
    val isRestDay: Boolean = false
)