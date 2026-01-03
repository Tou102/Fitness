package com.example.fitness.camera




import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs

class SquatCounter : RepCounter {

    private var state = SquatState.UP
    private var repCount = 0
    private var instruction = "Chuẩn bị squat"

    override fun process(landmarks: List<NormalizedLandmark>): Int {
        if (landmarks.size < 28) return repCount

        val hip = landmarks[23]     // left hip
        val knee = landmarks[25]    // left knee
        val ankle = landmarks[27]   // left ankle

        val kneeAngle = calculateAngle(hip, knee, ankle)

        when (state) {
            SquatState.UP -> {
                instruction = "Hạ xuống nào! ⬇️"
                if (kneeAngle < 110) {  // squat đủ sâu
                    state = SquatState.DOWN
                }
            }
            SquatState.DOWN -> {
                instruction = "Đứng lên! ⬆️"
                if (kneeAngle > 150) {
                    repCount++
                    state = SquatState.UP
                    instruction = "Tuyệt vời! Tiếp tục 🔥"
                }
            }
        }
        return repCount
    }

    override fun getInstruction(): String = instruction

    override fun reset() {
        state = SquatState.UP
        repCount = 0
        instruction = "Chuẩn bị squat"
    }

    private fun calculateAngle(
        a: NormalizedLandmark, b: NormalizedLandmark, c: NormalizedLandmark
    ): Double {
        val radians = kotlin.math.atan2(c.y() - b.y(), c.x() - b.x()) -
                kotlin.math.atan2(a.y() - b.y(), a.x() - b.x())
        var angle = abs(radians * 180.0 / kotlin.math.PI)
        if (angle > 180) angle = 360 - angle
        return angle
    }
}

private enum class SquatState { UP, DOWN }