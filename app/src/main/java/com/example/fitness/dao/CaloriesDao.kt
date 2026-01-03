package com.example.fitness.dao

import androidx.room.*
import com.example.fitness.entity.CaloriesRecordEntity

@Dao
interface CaloriesRecordDao {

    // ==================== INSERT / UPDATE / DELETE ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: CaloriesRecordEntity): Long // Trả về ID của bản ghi mới

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<CaloriesRecordEntity>)

    @Delete
    suspend fun delete(record: CaloriesRecordEntity)

    @Query("DELETE FROM calories_record WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calories_record")
    suspend fun deleteAll()

    // ==================== TRUY VẤN CƠ BẢN ====================
    @Query("SELECT * FROM calories_record ORDER BY timestamp DESC")
    suspend fun getAllRecords(): List<CaloriesRecordEntity>

    @Query("SELECT * FROM calories_record WHERE id = :id")
    suspend fun getRecordById(id: Long): CaloriesRecordEntity?

    // ==================== TRUY VẤN THEO THỜI GIAN ====================
    @Query("""
        SELECT * FROM calories_record 
        WHERE timestamp BETWEEN :startOfDay AND :endOfDay 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    suspend fun getTodayRecord(startOfDay: Long, endOfDay: Long): CaloriesRecordEntity?

    @Query("""
        SELECT * FROM calories_record 
        WHERE timestamp >= :startTimestamp 
        ORDER BY timestamp DESC
    """)
    suspend fun getRecordsSince(startTimestamp: Long): List<CaloriesRecordEntity>

    @Query("""
        SELECT * FROM calories_record 
        WHERE timestamp BETWEEN :from AND :to 
        ORDER BY timestamp DESC
    """)
    suspend fun getRecordsInRange(from: Long, to: Long): List<CaloriesRecordEntity>

    // ==================== THỐNG KÊ TỔNG HỢP ====================
    @Query("SELECT SUM(distance) FROM calories_record")
    suspend fun getTotalDistance(): Float?

    @Query("SELECT SUM(caloriesBurned) FROM calories_record")
    suspend fun getTotalCalories(): Float?

    @Query("SELECT SUM(runningTime) FROM calories_record")
    suspend fun getTotalRunningTime(): Long?

    @Query("SELECT COUNT(*) FROM calories_record")
    suspend fun getTotalSessions(): Int

    // ==================== THỐNG KÊ THEO NGÀY/THÁNG ====================
    // Tổng quãng đường trong 7 ngày gần nhất (dùng cho biểu đồ)
    @Query("""
        SELECT 
            strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') AS date,
            SUM(distance) AS dailyDistance
        FROM calories_record 
        WHERE timestamp >= :startOfWeek
        GROUP BY date
        ORDER BY date
    """)
    suspend fun getWeeklyDistance(startOfWeek: Long): List<DailyDistance>

    // Tổng calo theo ngày trong khoảng thời gian
    @Query("""
        SELECT 
            strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') AS date,
            SUM(caloriesBurned) AS dailyCalories
        FROM calories_record 
        WHERE timestamp BETWEEN :from AND :to
        GROUP BY date
        ORDER BY date
    """)
    suspend fun getDailyCaloriesInRange(from: Long, to: Long): List<DailyCalories>

    // ==================== SUPPORT DATA CLASS CHO THỐNG KÊ ====================
}

// Dùng cho truy vấn thống kê theo ngày (Room yêu cầu data class riêng)
data class DailyDistance(
    val date: String,        // YYYY-MM-DD
    val dailyDistance: Float
)

data class DailyCalories(
    val date: String,
    val dailyCalories: Float
)