package com.example.fitness.repository

import android.content.Context
import android.util.Log
import com.example.fitness.db.AppDatabase
import com.example.fitness.entity.FoodItem
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer

private const val TAG = "NutritionRepo"

/**
 * Repo chính:
 * 1) Ưu tiên JSON món Việt (vietnam_food.json trong assets)
 * 2) Nếu không có → dùng DB nội bộ (Room)
 * 3) Cuối cùng mới fallback sang API Ninjas
 */
class NutritionRepoLocalFirst(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val foodDao = db.foodDao()

    // Regex tách khối lượng: 100g, 200 ml, 150gram...
    private val qtyRegex =
        Regex(
            """\d+(?:\.\d+)?\s*(g|gram|grams|ml|milliliter|milliliters)""",
            RegexOption.IGNORE_CASE
        )

    // ---------- JSON local (món Việt) ----------

    // Model ánh xạ đúng với JSON bạn gửi
    private data class JsonFoodItem(
        val name: String,
        @SerializedName("serving_size_g") val servingSizeG: Double,
        val calories: Double,
        @SerializedName("protein_g") val proteinG: Double,
        @SerializedName("carbohydrates_total_g") val carbsG: Double,
        @SerializedName("fat_total_g") val fatG: Double,
        @SerializedName("fiber_g") val fiberG: Double,
        @SerializedName("sugar_g") val sugarG: Double
    )

    // Lazy load JSON một lần
    private val jsonFoods: List<JsonFoodItem> by lazy { loadJsonFoodsFromAssets() }

    private fun loadJsonFoodsFromAssets(): List<JsonFoodItem> {
        return try {
            val input = context.assets.open("foods_vi.json") // ← đổi lại tên này
            val json = input.bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<JsonFoodItem>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    private fun searchJsonByKey(key: String): JsonFoodItem? {
        if (key.isBlank()) return null

        val normalizedKey = key.trim()
        val match = jsonFoods.firstOrNull { item ->
            val normalizedName = removeDiacritics(item.name).lowercase()
            // ưu tiên so sánh bằng, nếu không thì contains
            normalizedName == normalizedKey || normalizedName.contains(normalizedKey)
        }

        Log.d(TAG, "searchJsonByKey key='$normalizedKey' -> ${match?.name}")
        return match
    }

    // ---------- API chính ----------

    suspend fun getNutritionInfo(rawQuery: String): String = withContext(Dispatchers.IO) {
        val query = rawQuery.trim()
        val weight = extractWeight(query)

        // Làm sạch tên (bỏ số + đơn vị), bỏ dấu → key
        val cleaned = query.replace(
            Regex(
                "\\d+(\\.|,)?\\d*\\s*(g|gram|grams|ml|milliliter|milliliters)?",
                RegexOption.IGNORE_CASE
            ),
            ""
        ).trim()
        val key = removeDiacritics(cleaned).lowercase()

        Log.d(TAG, "getNutritionInfo query='$query', cleaned='$cleaned', key='$key'")

        // 1) Ưu tiên JSON món Việt trước
        val jsonFood = searchJsonByKey(key)
        if (jsonFood != null) {
            Log.d(TAG, "Result from JSON: ${jsonFood.name}")
            return@withContext formatLocalJson(jsonFood, weight)
        }

        // 2) Nếu JSON không có thì tra DB (Room)
        val localFood = foodDao.searchByKey(key).firstOrNull()
        if (localFood != null) {
            Log.d(TAG, "Result from DB: ${localFood.name}")
            return@withContext formatLocal(localFood, weight)
        }

        // 3) Cuối cùng mới fallback sang API Ninjas
        return@withContext try {
            Log.d(TAG, "Fallback API for '$query'")
            NutritionRepo.getNutritionInfo(query)
        } catch (e: Exception) {
            Log.e(TAG, "API error: ${e.message}", e)
            "⚠️ Không thể lấy dữ liệu từ API: ${e.message ?: "unknown"}"
        }
    }

    // ---------- Format dữ liệu ----------

    /** Format dữ liệu món ăn trong DB (FoodItem – Room) */
    private fun formatLocal(f: FoodItem, userWeight: Double?): String {
        val base = f.servingSizeGram
        val ratio = if (userWeight != null && base > 0) userWeight / base else 1.0
        val lines = mutableListOf<String>()
        lines += "🍽 ${f.name}"
        lines += "• Khẩu phần gốc: ${base.f1()} g"
        if (userWeight != null && userWeight != base) {
            lines += "• Ước lượng theo bạn nhập: ${userWeight.f1()} g"
        }

        f.calories?.let { lines += "• Năng lượng: ${(it * ratio).f1()} kcal" }
        f.proteinG?.let { lines += "• Đạm: ${(it * ratio).f1()} g" }
        f.carbsG?.let { lines += "• Carb: ${(it * ratio).f1()} g" }
        f.fatG?.let { lines += "• Béo: ${(it * ratio).f1()} g" }
        f.fiberG?.let { if (it > 0.0) lines += "• Chất xơ: ${(it * ratio).f1()} g" }
        f.sugarG?.let { if (it > 0.0) lines += "• Đường: ${(it * ratio).f1()} g" }

        return lines.joinToString("\n")
    }

    /** Format dữ liệu món ăn trong JSON local */
    private fun formatLocalJson(f: JsonFoodItem, userWeight: Double?): String {
        val base = f.servingSizeG
        val ratio = if (userWeight != null && base > 0) userWeight / base else 1.0
        val lines = mutableListOf<String>()
        lines += "🍽 ${f.name}"
        lines += "• Khẩu phần gốc: ${base.f1()} g"
        if (userWeight != null && userWeight != base) {
            lines += "• Ước lượng theo bạn nhập: ${userWeight.f1()} g"
        }

        lines += "• Năng lượng: ${(f.calories * ratio).f1()} kcal"
        lines += "• Đạm: ${(f.proteinG * ratio).f1()} g"
        lines += "• Carb: ${(f.carbsG * ratio).f1()} g"
        lines += "• Béo: ${(f.fatG * ratio).f1()} g"
        if (f.fiberG > 0.0) lines += "• Chất xơ: ${(f.fiberG * ratio).f1()} g"
        if (f.sugarG > 0.0) lines += "• Đường: ${(f.sugarG * ratio).f1()} g"

        return lines.joinToString("\n")
    }

    // ---------- Helpers ----------

    private fun extractWeight(text: String): Double? {
        val match = qtyRegex.find(text) ?: return null
        return Regex("""(\d+(?:\.\d+)?)""").find(match.value)?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun Double.f1() = String.format(java.util.Locale.US, "%.1f", this)

    private fun removeDiacritics(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}
