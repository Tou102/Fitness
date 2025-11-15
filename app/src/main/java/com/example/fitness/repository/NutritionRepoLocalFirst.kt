package com.example.fitness.repository
import java.text.Normalizer
import android.content.Context
import com.example.fitness.db.AppDatabase

import com.example.fitness.entity.FoodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repo chính: ưu tiên lấy món ăn từ DB nội bộ (món Việt),
 * nếu không có thì fallback sang API Ninjas.
 */
class NutritionRepoLocalFirst(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val foodDao = db.foodDao()

    private val qtyRegex =
        Regex(
            """\d+(?:\.\d+)?\s*(g|gram|grams|ml|milliliter|milliliters)""",
            RegexOption.IGNORE_CASE
        )

    suspend fun getNutritionInfo(rawQuery: String): String = withContext(Dispatchers.IO) {
        val query = rawQuery.trim()
        val weight = extractWeight(query)

// Làm sạch tên (bỏ số + đơn vị), bỏ dấu → key
        val cleaned = query.replace(
            Regex("\\d+(\\.|,)?\\d*\\s*(g|gram|grams|ml|milliliter|milliliters)?", RegexOption.IGNORE_CASE),
            ""
        ).trim()
        val key = removeDiacritics(cleaned).lowercase()

// Tra DB theo nameKey
        val localFood = foodDao.searchByKey(key).firstOrNull()
        if (localFood != null) return@withContext formatLocal(localFood, weight)

// Fallback API
        return@withContext try { NutritionRepo.getNutritionInfo(query) }
        catch (e: Exception) { "⚠️ Không thể lấy dữ liệu từ API: ${e.message ?: "unknown"}" }

    }


    /** Format dữ liệu món ăn Việt (tính theo trọng lượng người nhập) */
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