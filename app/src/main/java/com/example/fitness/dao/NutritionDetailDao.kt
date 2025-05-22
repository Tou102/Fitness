package com.example.fitness.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fitness.entity.NutritionDetail

@Dao
interface NutritionDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nutritionDetail: NutritionDetail)

    @Update
    suspend fun update(nutritionDetail: NutritionDetail)

    @Delete
    suspend fun delete(nutritionDetail: NutritionDetail)

    @Query("SELECT * FROM nutrition_details ORDER BY id DESC")
    fun getAllNutritionDetails(): LiveData<List<NutritionDetail>>
}

