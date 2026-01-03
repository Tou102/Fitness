package com.example.fitness.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.SET_NULL

@Entity(
    tableName = "challenge_day_records",
    foreignKeys = [
        ForeignKey(
            entity = RunningChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = CaloriesRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["recordId"],
            onDelete = SET_NULL
        )
    ],
    indices = [Index(value = ["challengeId"]), Index(value = ["dayNumber"])]
)
data class ChallengeDayRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: String,
    val dayNumber: Int,                          // Ngày thứ mấy trong thử thách
    val recordId: Long?,                         // Liên kết với CaloriesRecordEntity.id (nullable)
    val completedDate: Long,                     // Timestamp ngày hoàn thành
    val actualDistanceMeters: Float,
    val actualTimeSeconds: Long,
    val isCompleted: Boolean = true
)