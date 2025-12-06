package com.example.fitness.repository

class CoachAdviceRepo {

    fun getAdvice(question: String): String {

        val q = question.lowercase()

        // ---- TƯ THẾ / FORM ----
        if (q.contains("tư thế") || q.contains("form") ||
            q.contains("squat") || q.contains("deadlift") ||
            q.contains("lưng") || q.contains("gối") || q.contains("tập đúng không")) {

            return """
🏋️ **Đánh giá tư thế & hướng dẫn form chuẩn**

• Giữ lưng **thẳng – neutral spine**, tránh cong/gù  
• Siết **core** trước khi xuống  
• Đầu gối hướng theo mũi chân  
• Di chuyển chậm – kiểm soát, không rơi tự do  
• Khi đứng dậy: đẩy lực từ **gót chân**  
• Hít vào khi xuống – thở ra khi đẩy lên

💡 Nếu muốn phân tích chi tiết hơn, hãy mô tả tư thế hoặc gửi hình/video.
""".trimIndent()
        }

        // ---- MỤC TIÊU – GOAL SETTING ----
        if (q.contains("mục tiêu") || q.contains("goal") ||
            q.contains("giảm cân") || q.contains("tăng cơ") ||
            q.contains("giảm mỡ") || q.contains("siết mỡ") ) {

            return """
🎯 **Tư vấn mục tiêu phù hợp**

• Hãy đặt mục tiêu theo nguyên tắc **SMART**  
  - Cụ thể  
  - Đo được  
  - Vừa sức  
  - Liên quan  
  - Có thời hạn  

• Ví dụ mục tiêu tốt:  
➡ "Trong 8 tuần giảm 3kg mỡ + tăng 1kg cơ"

• Lời khuyên:  
- Tăng cơ → tăng dần tạ 5–10% mỗi 2 tuần  
- Giảm mỡ → calo âm 300–400 kcal/ngày  
- Duy trì → tập 3–4 buổi/tuần + ăn đủ protein

🔥 Bạn gửi chiều cao – cân nặng – mục tiêu, mình lập plan miễn phí!
""".trimIndent()
        }

        // ---- ĂN UỐNG – NUTRITION ----
        if (q.contains("ăn") || q.contains("ăn gì") ||
            q.contains("diet") || q.contains("calo") ||
            q.contains("ăn uống") || q.contains("ăn như thế nào")) {

            return """
🥗 **Gợi ý ăn uống thông minh**

• Protein: 1.6 – 2.2g/kg (gà, trứng, bò, cá, sữa chua Hy Lạp)  
• Carb tốt: khoai lang, gạo lứt, yến mạch, trái cây  
• Fat tốt: bơ, olive, hạt, cá hồi  
• Ưu tiên đồ hấp, luộc, áp chảo  
• Tránh: chiên rán, trà sữa, đồ ngọt, rượu bia  

⚡ Nếu bạn muốn mình tính calo chuẩn theo TDEE thì gửi:  
➡ Tuổi – chiều cao – cân nặng – mức vận động
""".trimIndent()
        }

        // ---- Default ----
        return """
🤖 Mình chưa rõ bạn muốn hỏi về:

• 🏋️ Tư thế tập – form của bài tập  
• 🎯 Mục tiêu tập luyện  
• 🥗 Ăn uống – dinh dưỡng  

Bạn nói rõ hơn để mình hỗ trợ nha!
""".trimIndent()
    }
}
