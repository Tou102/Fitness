package com.example.fitness.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitness.entity.FoodItem

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodItem>)

    // tìm bằng khóa không dấu
    @Query("SELECT * FROM food_items WHERE nameKey LIKE '%' || :q || '%' LIMIT 20")
    suspend fun searchByKey(q: String): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE nameKey = :key LIMIT 1")
    suspend fun getByExactKey(key: String): FoodItem?
}
