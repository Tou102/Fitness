package com.example.fitness.repository

import com.example.fitness.dao.CaloriesRecordDao
import com.example.fitness.dao.DailyCalories   // ← Thêm import này
import com.example.fitness.dao.DailyDistance // ← Thêm import này
import com.example.fitness.entity.CaloriesRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CaloriesRepository(private val caloriesRecordDao: CaloriesRecordDao) {

    // ==================== INSERT / DELETE ====================
    suspend fun insert(record: CaloriesRecordEntity): Long = withContext(Dispatchers.IO) {
        caloriesRecordDao.insert(record)
    }

    suspend fun delete(record: CaloriesRecordEntity) = withContext(Dispatchers.IO) {
        caloriesRecordDao.delete(record)
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        caloriesRecordDao.deleteById(id)
    }

    // ==================== LẤY DỮ LIỆU CƠ BẢN ====================
    suspend fun getAllRecords(): List<CaloriesRecordEntity> = withContext(Dispatchers.IO) {
        caloriesRecordDao.getAllRecords()
    }

    suspend fun getRecordById(id: Long): CaloriesRecordEntity? = withContext(Dispatchers.IO) {
        caloriesRecordDao.getRecordById(id)
    }

    // ==================== THỐNG KÊ TỔNG HỢP ====================
    suspend fun getTotalDistance(): Float = withContext(Dispatchers.IO) {
        caloriesRecordDao.getTotalDistance() ?: 0f
    }

    suspend fun getTotalCalories(): Float = withContext(Dispatchers.IO) {
        caloriesRecordDao.getTotalCalories() ?: 0f
    }

    suspend fun getTotalRunningTime(): Long = withContext(Dispatchers.IO) {
        caloriesRecordDao.getTotalRunningTime() ?: 0L
    }

    suspend fun getTotalSessions(): Int = withContext(Dispatchers.IO) {
        caloriesRecordDao.getTotalSessions()
    }

    // ==================== THEO THỜI GIAN ====================
    suspend fun getTodayRecord(startOfDay: Long, endOfDay: Long): CaloriesRecordEntity? = withContext(Dispatchers.IO) {
        caloriesRecordDao.getTodayRecord(startOfDay, endOfDay)
    }

    suspend fun getRecordsSince(startTimestamp: Long): List<CaloriesRecordEntity> = withContext(Dispatchers.IO) {
        caloriesRecordDao.getRecordsSince(startTimestamp)
    }

    suspend fun getRecordsInRange(from: Long, to: Long): List<CaloriesRecordEntity> = withContext(Dispatchers.IO) {
        caloriesRecordDao.getRecordsInRange(from, to)
    }

    // ==================== THỐNG KÊ THEO NGÀY (CHO BIỂU ĐỒ) ====================
    // SỬA Ở ĐÂY: Chỉ dùng DailyDistance và DailyCalories (không cần CaloriesRecordDao.)
    suspend fun getWeeklyDistance(startOfWeek: Long): List<DailyDistance> = withContext(Dispatchers.IO) {
        caloriesRecordDao.getWeeklyDistance(startOfWeek)
    }

    suspend fun getDailyCaloriesInRange(from: Long, to: Long): List<DailyCalories> = withContext(Dispatchers.IO) {
        caloriesRecordDao.getDailyCaloriesInRange(from, to)
    }
}