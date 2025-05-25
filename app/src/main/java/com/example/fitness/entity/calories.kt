package com.example.fitness.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calories_record")
data class CaloriesRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caloriesBurned: Float,
    val timestamp: Long,
    val distance: Float,
    val runningTime: Long
)
