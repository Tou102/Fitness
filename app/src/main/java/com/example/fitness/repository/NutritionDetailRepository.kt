package com.example.fitness.repository

import com.example.fitness.dao.NutritionDetailDao
import com.example.fitness.entity.NutritionDetail

class NutritionDetailRepository(private val nutritionDetailDao: NutritionDetailDao) {

    suspend fun getAllNutritionDetails(): List<NutritionDetail> = nutritionDetailDao.getAllNutritionDetails()

    suspend fun insert(nutritionDetail: NutritionDetail) {
        nutritionDetailDao.insert(nutritionDetail)
    }

    suspend fun update(nutritionDetail: NutritionDetail) {
        nutritionDetailDao.update(nutritionDetail)
    }

    suspend fun delete(nutritionDetail: NutritionDetail) {
        nutritionDetailDao.delete(nutritionDetail)
    }

    suspend fun getById(id: Long): NutritionDetail? = nutritionDetailDao.getNutritionDetailById(id)

    suspend fun getNutritionDetailsByGroup(groupName: String): List<NutritionDetail> =
        nutritionDetailDao.getNutritionDetailsByGroup(groupName)

}
