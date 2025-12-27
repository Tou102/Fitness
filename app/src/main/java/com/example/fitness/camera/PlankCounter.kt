package com.example.fitness.camera

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.*
class PlankCounter : RepCounter {

    private var lastCorrectTime = 0L
    private var totalSecondsHeld = 0
    private var currentInstruction = "Sẵn sàng"

    override fun process(landmarks: List<NormalizedLandmark>): Int {
        if (landmarks.size <= 28) return totalSecondsHeld

        val shoulder = landmarks[11]
        val hip = landmarks[23]
        val knee = landmarks[25]
        val ankle = landmarks[27]
        val wrist = landmarks[15]

        val hipAngle = calculateAngle(shoulder, hip, ankle)
        val kneeAngle = calculateAngle(hip, knee, ankle)
        val shoulderHipDiffY = abs(shoulder.y() - hip.y())

        // SỬA ĐIỀU KIỆN: nới lỏng và sửa wrist.y()
        val isGoodPosture = hipAngle > 150 && hipAngle < 210 &&      // nới rộng
                kneeAngle > 150 && kneeAngle < 210 &&
                shoulderHipDiffY < 0.2 &&               // từ 0.1 → 0.2 (linh hoạt hơn)
                wrist.y() > shoulder.y() + 0.05        // SỬA: wrist phải DƯỚI vai (y lớn hơn)

        if (isGoodPosture) {
            currentInstruction = "Giữ tốt! 🔥"

            val currentTime = System.currentTimeMillis()
            if (lastCorrectTime == 0L) {
                lastCorrectTime = currentTime
            } else if (currentTime - lastCorrectTime >= 1000) {
                totalSecondsHeld++
                lastCorrectTime = currentTime
            }
        } else {
            lastCorrectTime = 0L
            currentInstruction = when {
                shoulderHipDiffY > 0.2 -> "Giữ thẳng lưng! ️"
                hipAngle <= 150 -> "Hạ mông xuống ⬇️"
                hipAngle >= 210 -> "Nâng hông lên ⬆️"
                wrist.y() <= shoulder.y() + 0.05 -> "Giữ vững nào "
                else -> "Cố lên "
            }
        }

        return totalSecondsHeld
    }
    override fun getInstruction(): String {
        return currentInstruction
    }

    // --- [SỬA LỖI] THÊM HÀM RESET VÀO ĐÂY ---
    override fun reset() {
        lastCorrectTime = 0L
        totalSecondsHeld = 0
        currentInstruction = "Sẵn sàng"
    }

    // --- HÀM TOÁN HỌC TÍNH GÓC ---
    private fun calculateAngle(
        a: NormalizedLandmark,
        b: NormalizedLandmark,
        c: NormalizedLandmark
    ): Double {
        val radians = atan2(c.y() - b.y(), c.x() - b.x()) -
                atan2(a.y() - b.y(), a.x() - b.x())
        var angle = abs(radians * 180.0 / Math.PI)

        if (angle > 180.0) {
            angle = 360.0 - angle
        }
        return angle
    }
}