package com.example.fitness


object AiCoach {

    /** paceMinPerKm: phút/km (vd 5.20 nghĩa là 5'12") */
    fun recommendNextRun(
        paceMinPerKm: Double?, // null nếu chưa đủ dữ liệu
        rpe: Int,              // 1..10
        goal: String           // "Giảm cân" | "5K" | "10K" | ...
    ): String {
        val base = when {
            rpe >= 8 -> "Buổi tới nên phục hồi: chạy nhẹ 30–40’ ở vùng 2."
            rpe <= 5 -> "Bạn đang khoẻ: thêm 20–25’ tempo hoặc tăng 1km so với tuần trước."
            else     -> "Duy trì cường độ hiện tại, tập đều 3–4 buổi/tuần."
        }

        val byGoal = when (goal.lowercase()) {
            "giảm cân" -> " Giữ nhịp ở vùng 2 (60–70% HRmax) để tối ưu đốt mỡ."
            "5k"       -> " Thêm 1 buổi interval ngắn 5×400m, nghỉ 200m."
            "10k"      -> " Duy trì 1 buổi tempo 20–30’/tuần và long run Z2."
            "21k","half"-> " Long run tăng dần (10–16km), giữ 80/20 easy/hard."
            else       -> ""
        }

        val paceHint = paceMinPerKm?.let {
            " Gợi ý pace buổi dễ: ~${String.format("%.2f", it + 0.20)} phút/km; tempo: ~${String.format("%.2f", it - 0.15)}."
        } ?: ""

        return base + byGoal + paceHint
    }

    /** Điều chỉnh volume tuần kế tiếp theo RPE + drift (nếu có) */
    fun adaptWeeklyVolume(prevKm: Int, rpe: Int, cardiacDriftPct: Double?): Int {
        val dec = (rpe >= 8) || ((cardiacDriftPct ?: 0.0) > 7.0)
        val inc = (rpe <= 5) && ((cardiacDriftPct ?: 0.0) < 4.0)
        return when {
            dec -> (prevKm * 0.90).toInt()
            inc -> (prevKm * 1.05).toInt()
            else -> prevKm
        }.coerceAtLeast(15)
    }
}
