package com.example.fitness.repository

import com.example.fitness.dao.CaloriesRecordDao
import com.example.fitness.entity.CaloriesRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CaloriesRepository(private val caloriesRecordDao: CaloriesRecordDao) {

    suspend fun insert(record: CaloriesRecordEntity) {
        caloriesRecordDao.insert(record)
    }

    suspend fun delete(record: CaloriesRecordEntity) {
        caloriesRecordDao.delete(record)
    }

    // Lấy tất cả các bản ghi từ cơ sở dữ liệu
    suspend fun getAllRecords(): List<CaloriesRecordEntity> {
        return caloriesRecordDao.getAll()
    }
}




