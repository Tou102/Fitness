package com.example.fitness.camera

object WorkoutRepository {

    // Hàm lấy danh sách bài tập theo Level
    fun getWorkoutPlan(level: DifficultyLevel): List<ExerciseScheduleItem> {
        return when (level) {
            DifficultyLevel.EASY -> listOf( // --- MỨC 1 ---
                ExerciseScheduleItem(1, "Hít đất 1", ExerciseType.PUSH_UP, targetValue = 5, unit = "cái"),
                ExerciseScheduleItem(2, "Squat 1", ExerciseType.SQUAT, targetValue = 5, unit = "cái"),
                ExerciseScheduleItem(3, "Plank 1", ExerciseType.PLANK, targetValue = 30, unit = "giây") // Plank 30s
            )

            DifficultyLevel.MEDIUM -> listOf( // --- MỨC 2 ---
                ExerciseScheduleItem(1, "Hít đất 2", ExerciseType.PUSH_UP, targetValue = 20, unit = "cái"),
                ExerciseScheduleItem(2, "Squat 2", ExerciseType.SQUAT, targetValue = 20, unit = "cái"),
                ExerciseScheduleItem(3, "Plank 2", ExerciseType.PLANK, targetValue = 60, unit = "giây") // Plank 60s
            )

            DifficultyLevel.HARD -> listOf( // --- MỨC 3 ---
                ExerciseScheduleItem(1, "Hít đất 3", ExerciseType.PUSH_UP, targetValue = 50, unit = "cái"),
                ExerciseScheduleItem(2, "Squat 3", ExerciseType.SQUAT, targetValue = 50, unit = "cái"),
                ExerciseScheduleItem(3, "Plank 3", ExerciseType.PLANK, targetValue = 120, unit = "giây")
            )
        }
    }
}