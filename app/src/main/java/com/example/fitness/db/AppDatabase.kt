package com.example.fitness.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitness.dao.CaloriesRecordDao
import com.example.fitness.dao.ExerciseDao

import com.example.fitness.dao.UserDao
import com.example.fitness.entity.Exercise
import com.example.fitness.entity.User

import com.example.fitness.dao.NutritionDetailDao




import com.example.fitness.dao.WaterIntakeDao
import com.example.fitness.dao.WorkoutSessionDao
import com.example.fitness.entity.CaloriesRecordEntity


import com.example.fitness.entity.NutritionDetail


import com.example.fitness.entity.WaterIntakeRecordEntity
import com.example.fitness.entity.WorkoutSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




@Database(entities = [User::class, Exercise::class, NutritionDetail::class, CaloriesRecordEntity::class, WaterIntakeRecordEntity::class, WorkoutSession::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun userDao(): UserDao
    abstract fun caloriesRecordDao(): CaloriesRecordDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun NutritionDetailDao(): NutritionDetailDao


    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())  // <-- Thêm callback tại đây
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    // Kiểm tra user admin đã tồn tại chưa
                    val existingAdmin = database.userDao().getUserByUsername("admin")
                    if (existingAdmin == null) {
                        val adminUser = User(
                            username = "admin",
                            password = "123",
                            name = "Administrator",
                            role = "admin"  // Nhớ thêm trường role trong User entity
                        )
                        database.userDao().insert(adminUser)
                    }
                }
            }
        }
    }
}


