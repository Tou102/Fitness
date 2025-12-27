package com.example.fitness.camera

object CounterFactory {
    fun getCounter(type: ExerciseType): RepCounter? {
        return when (type) {
            ExerciseType.PUSH_UP -> PushUpCounter()

            ExerciseType.SQUAT -> SquatCounter()
            ExerciseType.PLANK -> PlankCounter()
            else -> null
        }
    }
}