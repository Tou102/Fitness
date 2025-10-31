package com.example.fitness.repository

import com.example.fitness.data.remote.NetworkModule
import com.example.fitness.data.remote.ninjas.NinjaFood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.Locale

object NutritionRepo {

    // Nhận dạng có lượng + đơn vị (g/ml) trong câu người dùng hay chưa
    private val qtyRegex =
        Regex("""\d+(?:\.\d+)?\s*(g|gram|grams|ml|milliliter|milliliters)""", RegexOption.IGNORE_CASE)

    // Format 1 chữ số thập phân, cố định theo US
    private fun Double.f1(): String = String.format(Locale.US, "%.1f", this)

    suspend fun getNutritionInfo(rawQuery: String): String = withContext(Dispatchers.IO) {
        try {
            // Chuẩn hoá câu hỏi để API hiểu (nếu thiếu lượng -> thêm "100g" cho request)
            val queryForApi = preprocess(rawQuery)
            // Gọi API (header X-Api-Key đã được gắn sẵn ở OkHttp Interceptor)
            val foods: List<NinjaFood> = NetworkModule.ninjasApi.getNutrition(queryForApi)

            if (foods.isEmpty()) {
                return@withContext "Không tìm thấy món '$queryForApi'. Hãy ghi rõ món + lượng (vd: \"pho ga 300g\")."
            }

            val f = foods.first()

            // Khối lượng người dùng nhập (vd: 500 từ "pho ga 500g"), nếu có
            val userWeight = extractWeight(rawQuery)
            // Khẩu phần gốc server trả (thường 100g hoặc 1 serving)
            val baseWeight = f.serving_size_g ?: 100.0
            // Tỷ lệ để scale các số liệu theo lượng người dùng nhập
            val ratio = if (userWeight != null && baseWeight > 0.0) userWeight / baseWeight else 1.0

            val name = f.name?.ifBlank { null } ?: "Món ăn"
            val lines = mutableListOf<String>()
            lines += "🍽 $name"

            // Hiển thị rõ khẩu phần gốc + (nếu có) khẩu phần ước lượng theo người dùng
            lines += "• Khẩu phần gốc: ${baseWeight.f1()} g"
            if (userWeight != null && userWeight != baseWeight) {
                lines += "• Ước lượng theo khẩu phần bạn nhập: ${userWeight.f1()} g"
            }

            // Chỉ in các dòng có dữ liệu; nếu có userWeight thì nhân theo ratio
            f.calories?.let                  { lines += "• Năng lượng: ${(it * ratio).f1()} kcal" }
            f.protein_g?.let                 { lines += "• Đạm: ${(it * ratio).f1()} g" }
            f.carbohydrates_total_g?.let     { lines += "• Carb: ${(it * ratio).f1()} g" }
            f.fat_total_g?.let               { lines += "• Béo: ${(it * ratio).f1()} g" }
            f.fiber_g?.let                   { if (it > 0.0) lines += "• Chất xơ: ${(it * ratio).f1()} g" }
            f.sugar_g?.let                   { if (it > 0.0) lines += "• Đường: ${(it * ratio).f1()} g" }

            // Nếu free-tier khiến hầu hết số liệu null → báo rõ
            if (lines.size <= 2) { // chỉ có tiêu đề + khẩu phần gốc
                lines += "• Dữ liệu chi tiết cho mục này không khả dụng ở gói miễn phí."
            }

            lines.joinToString("\n")
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()?.take(300).orEmpty()
            "⚠️ Lỗi khi lấy dữ liệu (HTTP ${e.code()}): ${body.ifBlank { e.message() ?: "Bad request" }}"
        } catch (e: Exception) {
            "⚠️ Lỗi mạng/khác: ${e.message ?: "unknown"}"
        }
    }

    /** Nếu người dùng không ghi lượng → thêm 100g để API hiểu */
    private fun preprocess(input: String): String {
        val t = input.trim()
        return if (qtyRegex.containsMatchIn(t)) t else "$t 100g"
    }

    /** Trích khối lượng người dùng nhập (vd: "500g" -> 500.0) */
    private fun extractWeight(text: String): Double? {
        val m = qtyRegex.find(text)
        return m?.groupValues?.get(0)
            ?.let { Regex("""(\d+(?:\.\d+)?)""").find(it)?.groupValues?.get(1)?.toDoubleOrNull() }
    }
}
