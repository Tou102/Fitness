package com.example.fitness.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitness.dao.CaloriesRecordDao

import com.example.fitness.dao.FoodDao
import com.example.fitness.dao.NutritionDetailDao
import com.example.fitness.dao.UserExerciseDao
import com.example.fitness.dao.WaterIntakeDao
import com.example.fitness.dao.WorkoutSessionDao
import com.example.fitness.entity.CaloriesRecordEntity

import com.example.fitness.entity.FoodItem
import com.example.fitness.entity.NutritionDetail
import com.example.fitness.entity.User
import com.example.fitness.entity.UserExercise
import com.example.fitness.entity.WaterIntakeRecordEntity
import com.example.fitness.entity.WorkoutSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.Normalizer

@Database(
    entities = [
        User::class,
        NutritionDetail::class,
        CaloriesRecordEntity::class,
        WaterIntakeRecordEntity::class,
        WorkoutSession::class,
        FoodItem::class,                 // <- có trường nameKey trong entity
        UserExercise::class
    ],
    version = 14,                        // bump để recreate & seed lại
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): com.example.fitness.dao.UserDao
    abstract fun caloriesRecordDao(): CaloriesRecordDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun NutritionDetailDao(): NutritionDetailDao
    abstract fun foodDao(): FoodDao
    abstract fun userExerciseDao(): UserExerciseDao

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
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val appContext: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    // seed tài khoản admin như cũ
                    val existingAdmin = database.userDao().getUserByUsername("admin")
                    if (existingAdmin == null) {
                        val adminUser = User(
                            username = "admin",
                            password = "123",
                            name = "Administrator",
                            role = "admin"
                        )
                        database.userDao().insert(adminUser)
                    }

                    // seed món ăn Việt từ assets/coach_dataset.json
                    try {
                        val seed = readFoodsFromAssets(appContext, "coach_dataset.json")
                        if (seed.isNotEmpty()) database.foodDao().insertAll(seed)
                    } catch (_: Exception) { /* log nếu cần */ }
                }
            }
        }

        /** Đọc JSON array từ assets và map sang List<FoodItem> (tính sẵn nameKey = tên không dấu, lowercase) */
        private fun readFoodsFromAssets(context: Context, filename: String): List<FoodItem> {
            return try {
                val text = context.assets.open(filename).bufferedReader().use { it.readText() }
                val arr = JSONArray(text)
                val list = ArrayList<FoodItem>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val name = o.getString("name")
                    val nameKey = removeDiacritics(name).lowercase()

                    list.add(
                        FoodItem(
                            name = name,
                            nameKey = nameKey, // <- khóa tìm kiếm không dấu
                            servingSizeGram = o.optDoubleOrNull("serving_size_g") ?: 100.0,
                            calories = o.optDoubleOrNull("calories"),
                            proteinG = o.optDoubleOrNull("protein_g"),
                            carbsG = o.optDoubleOrNull("carbohydrates_total_g"),
                            fatG = o.optDoubleOrNull("fat_total_g"),
                            fiberG = o.optDoubleOrNull("fiber_g"),
                            sugarG = o.optDoubleOrNull("sugar_g")
                        )
                    )
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }

        // Helper: null nếu không có/không phải số
        private fun org.json.JSONObject.optDoubleOrNull(key: String): Double? =
            if (has(key) && !isNull(key)) {
                when (val v = opt(key)) {
                    is Number -> v.toDouble()
                    is String -> v.toDoubleOrNull()
                    else -> null
                }
            } else null

        // Bỏ dấu tiếng Việt
        private fun removeDiacritics(text: String): String {
            val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
            return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        }
    }
}
