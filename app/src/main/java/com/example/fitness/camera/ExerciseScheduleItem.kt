package com.example.fitness.camera

// 1. Định danh các loại bài tập hỗ trợ AI
enum class ExerciseType {
    PUSH_UP,
    SQUAT,
    PLANK, // Ví dụ thêm

}
enum class DifficultyLevel {
    EASY,   // Mức 1
    MEDIUM, // Mức 2
    HARD    // Mức 3
}

data class ExerciseScheduleItem(
    val id: Int,
    val name: String,         // Ví dụ: "Hít đất khởi động"
    val type: ExerciseType,   // PUSH_UP, SQUAT, PLANK
    val targetValue: Int,     // Số lần (Reps) hoặc Số giây (Seconds)
    val unit: String = "cái"  // "cái" hoặc "giây"
)