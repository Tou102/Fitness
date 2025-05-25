package com.example.fitness.dao



import androidx.room.*
import com.example.fitness.entity.WaterIntakeRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM water_intake ORDER BY timestamp DESC")
    fun getAllRecords(): kotlinx.coroutines.flow.Flow<List<WaterIntakeRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: WaterIntakeRecordEntity)

    @Query("DELETE FROM water_intake WHERE id = :id")
    suspend fun deleteById(id: Int)
}

