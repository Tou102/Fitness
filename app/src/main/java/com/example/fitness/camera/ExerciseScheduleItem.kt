package com.example.fitness.camera
// Nếu muốn tách riêng: package com.example.fitness.data

/**
 * Các loại bài tập mà app sẽ hỗ trợ nhận diện bằng AI (Camera + Pose Detection)
 * Bạn có thể thêm dần các bài tập mới ở đây.
 */
enum class ExerciseType {
    PUSH_UP,    // Hít đất
    SQUAT,      // Squat / Gánh tạ không tải
    PLANK,      // Giữ plank
    LUNGE,      // Chùng chân (Lunge)
    JUMPING_JACK,
    SIT_UP,
    BURPEE
    // Thêm các bài khác khi mở rộng...
}

/**
 * Mức độ khó của buổi tập / bài tập
 */
enum class DifficultyLevel {
    EASY,   // Mức 1 - Người mới bắt đầu
    MEDIUM, // Mức 2 - Trung bình
    HARD    // Mức 3 - Nâng cao / Pro
}

/**
 * Một mục trong lịch tập (schedule) hoặc trong một buổi tập
 * Ví dụ: 10 cái hít đất, giữ plank 30 giây,...
 */
data class ExerciseScheduleItem(
    val id: Int,                    // ID duy nhất để phân biệt (có thể dùng cho key trong LazyColumn)
    val name: String,               // Tên hiển thị cho người dùng, ví dụ: "Hít đất chuẩn"
    val type: ExerciseType,         // Loại bài tập để AI nhận diện
    val targetValue: Int,           // Số lượng mục tiêu: reps hoặc seconds
    val unit: String = "cái",       // "cái" cho reps, "giây" cho thời gian
    val description: String = ""    // Mô tả thêm (tùy chọn): "Giữ lưng thẳng, khuỷu tay 90°"
)