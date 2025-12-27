package com.example.fitness.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitness.entity.WorkoutSession
import com.example.fitness.getWeekStartEndTimestamps
import kotlinx.coroutines.flow.Flow


@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    suspend fun getSessionsInPeriod(userId: Int, startDate: Long, endDate: Long): List<WorkoutSession>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession)


    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC")
    fun getAllSessions(userId: Int): Flow<List<WorkoutSession>>
    // Các hàm khác như updateSession, deleteSession nếu cần
}



