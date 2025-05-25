package com.example.fitness.dao
import androidx.room.*
import com.example.fitness.entity.NutritionDetail

@Dao
interface NutritionDetailDao {

    @Insert
    suspend fun insert(nutritionDetail: NutritionDetail)

    @Update
    suspend fun update(nutritionDetail: NutritionDetail)

    @Delete
    suspend fun delete(nutritionDetail: NutritionDetail)

    @Query("SELECT * FROM nutrition_details ORDER BY id DESC")
    suspend fun getAllNutritionDetails(): List<NutritionDetail>

    @Query("SELECT * FROM nutrition_details WHERE id = :id")
    suspend fun getNutritionDetailById(id: Long): NutritionDetail?

    @Query("SELECT * FROM nutrition_details WHERE groupName = :groupName ORDER BY id DESC")
    suspend fun getNutritionDetailsByGroup(groupName: String): List<NutritionDetail>

}


