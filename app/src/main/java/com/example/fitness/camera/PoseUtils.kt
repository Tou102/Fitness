package com.example.fitness.camera

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.PI

object PoseUtils {
    // Hàm tính góc giữa 3 điểm A - B - C
    fun calculateAngle(
        a: NormalizedLandmark,
        b: NormalizedLandmark,
        c: NormalizedLandmark
    ): Double {
        val radians = atan2(c.y() - b.y(), c.x() - b.x()) -
                atan2(a.y() - b.y(), a.x() - b.x())
        var angle = abs(radians * 180.0 / PI)

        if (angle > 180.0) {
            angle = 360.0 - angle
        }
        return angle
    }
}