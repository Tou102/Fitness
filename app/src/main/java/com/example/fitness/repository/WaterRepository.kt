package com.example.fitness.repository

import com.example.fitness.dao.WaterIntakeDao
import com.example.fitness.entity.WaterIntakeRecordEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WaterIntakeRepository(private val dao: WaterIntakeDao) {
    suspend fun getAllRecords() = dao.getAllRecords()
    suspend fun insert(record: WaterIntakeRecordEntity) = dao.insert(record)
    suspend fun deleteById(id: Int) = dao.deleteById(id)
}
