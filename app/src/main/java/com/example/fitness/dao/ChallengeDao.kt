package com.example.fitness.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.fitness.entity.ChallengeDayRecordEntity
import com.example.fitness.entity.DailyGoalEntity
import com.example.fitness.entity.RunningChallengeEntity

@Dao
interface ChallengeDao {

    // ==================== THỬ THÁCH CHÍNH ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: RunningChallengeEntity)

    @Query("SELECT * FROM running_challenges WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveChallenge(): RunningChallengeEntity?

    @Query("SELECT * FROM running_challenges WHERE id = :challengeId")
    suspend fun getChallengeById(challengeId: String): RunningChallengeEntity?

    @Query("UPDATE running_challenges SET isActive = 0, isCompleted = :completed WHERE id = :challengeId")
    suspend fun finishChallenge(challengeId: String, completed: Boolean = true)

    // ==================== MỤC TIÊU HÀNG NGÀY ====================
    @Insert
    suspend fun insertDailyGoals(goals: List<DailyGoalEntity>)

    @Query("SELECT * FROM daily_goals WHERE challengeId = :challengeId AND dayNumber = :dayNumber LIMIT 1")
    suspend fun getGoalForDay(challengeId: String, dayNumber: Int): DailyGoalEntity?

    @Query("SELECT * FROM daily_goals WHERE challengeId = :challengeId ORDER BY dayNumber")
    suspend fun getAllGoalsForChallenge(challengeId: String): List<DailyGoalEntity>

    // ==================== HOÀN THÀNH NGÀY ====================
    @Insert
    suspend fun insertDayRecord(record: ChallengeDayRecordEntity)

    @Query("""
        SELECT * FROM challenge_day_records 
        WHERE challengeId = :challengeId AND dayNumber = :dayNumber 
        LIMIT 1
    """)
    suspend fun getDayRecord(challengeId: String, dayNumber: Int): ChallengeDayRecordEntity?

    @Query("SELECT COUNT(*) FROM challenge_day_records WHERE challengeId = :challengeId")
    suspend fun getCompletedDaysCount(challengeId: String): Int

    @Query("""
        SELECT * FROM challenge_day_records 
        WHERE challengeId = :challengeId 
        ORDER BY dayNumber DESC
    """)
    suspend fun getAllCompletedDays(challengeId: String): List<ChallengeDayRecordEntity>

    // ==================== TÍNH STREAK (chuỗi ngày liên tục) ====================
    @Transaction
    @Query("""
        SELECT * FROM challenge_day_records 
        WHERE challengeId = :challengeId 
        ORDER BY completedDate DESC
    """)
    suspend fun getCompletedRecordsOrdered(challengeId: String): List<ChallengeDayRecordEntity>

    // ==================== XÓA DỮ LIỆU (khi cần reset thử thách) ====================
    @Query("DELETE FROM daily_goals WHERE challengeId = :challengeId")
    suspend fun deleteGoalsForChallenge(challengeId: String)

    @Query("DELETE FROM challenge_day_records WHERE challengeId = :challengeId")
    suspend fun deleteRecordsForChallenge(challengeId: String)
}