package com.example.fitness.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitness.dao.*
import com.example.fitness.entity.*
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
        FoodItem::class,
        UserExercise::class,

        // === THÊM MỚI: HỖ TRỢ THỬ THÁCH CHẠY BỘ DÀI HẠN ===
        RunningChallengeEntity::class,
        DailyGoalEntity::class,
        ChallengeDayRecordEntity::class
    ],
    version = 15, // Tăng từ 14 → 15
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO cũ
    abstract fun userDao(): UserDao
    abstract fun caloriesRecordDao(): CaloriesRecordDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun NutritionDetailDao(): NutritionDetailDao
    abstract fun foodDao(): FoodDao
    abstract fun userExerciseDao(): UserExerciseDao

    // DAO mới cho thử thách
    abstract fun challengeDao(): ChallengeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_database"
                )
                    .fallbackToDestructiveMigration() // Dùng tạm nếu đang dev, sau này nên viết migration
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
                    // Seed tài khoản admin
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

                    // Seed món ăn Việt từ assets

                }
            }
        }
    }

    /** Đọc JSON array từ assets và map sang List<FoodItem> */
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
                        nameKey = nameKey,
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

    // Helper: lấy Double an toàn
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