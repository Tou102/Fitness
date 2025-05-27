package com.example.fitness.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_details")
data class NutritionDetail(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val title: String,
    val description: String,
    val imageUri: String?,
    val groupName: String,
)
