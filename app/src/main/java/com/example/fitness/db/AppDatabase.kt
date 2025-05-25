package com.example.fitness.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.fitness.dao.ExerciseDao

import com.example.fitness.dao.NutritionDetailDao
import com.example.fitness.dao.UserDao
import com.example.fitness.entity.Exercise
import com.example.fitness.entity.NutritionDetail
import com.example.fitness.entity.User

@Database(entities = [User::class, Exercise::class, NutritionDetail::class], version = 3)





abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun userDao(): UserDao
    abstract fun NutritionDetailDao(): NutritionDetailDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
