package com.example.fitness.dao

import androidx.room.*
import com.example.fitness.entity.CaloriesRecordEntity

@Dao
interface CaloriesRecordDao {

    @Insert
    suspend fun insert(record: CaloriesRecordEntity)

    @Delete
    suspend fun delete(record: CaloriesRecordEntity)

    // Truy vấn lấy tất cả bản ghi
    @Query("SELECT * FROM CALORIES_RECORD")
    suspend fun getAll(): List<CaloriesRecordEntity>
}

