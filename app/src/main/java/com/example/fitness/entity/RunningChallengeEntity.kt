package com.example.fitness.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_challenges")
data class RunningChallengeEntity(
    @PrimaryKey val id: String,                  // UUID.randomUUID().toString()
    val name: String,                            // Tên thử thách, ví dụ: "100 ngày chạy bộ liên tục"
    val totalDays: Int,                          // Tổng số ngày: 50, 75, 100...
    val startDate: Long,                         // Timestamp (ms) ngày bắt đầu thử thách
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,                // Còn đang diễn ra không
    val isCompleted: Boolean = false             // Đã hoàn thành 100% chưa
)