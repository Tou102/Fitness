package com.example.fitness.camera

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class PushUpCounter : RepCounter {

    private var isDown = false // Trạng thái: false = đang ở trên, true = đang ở dưới
    private var count = 0
    private var currentInstruction = "Sẵn sàng"

    // Ngưỡng góc (Bạn có thể tinh chỉnh sau)
    private val DOWN_ANGLE = 80.0  // Góc khuỷu tay khi xuống
    private val UP_ANGLE = 160.0   // Góc khuỷu tay khi lên

    override fun process(landmarks: List<NormalizedLandmark>): Int {
        // Lấy các điểm mốc bên TRÁI (Left): Vai(11), Khuỷu(13), Cổ tay(15)
        // (Lưu ý: Logic xịn hơn thì nên check xem người dùng quay bên nào để lấy trái/phải)
        val shoulder = landmarks[11]
        val elbow = landmarks[13]
        val wrist = landmarks[15]

        // Tính góc khuỷu tay
        val angle = PoseUtils.calculateAngle(shoulder, elbow, wrist)

        // Máy trạng thái (State Machine)
        if (angle < DOWN_ANGLE) {
            // Đang xuống sâu
            isDown = true
            currentInstruction = "Lên nào!"
        } else if (angle > UP_ANGLE) {
            // Đang tay thẳng
            if (isDown) {
                // Nếu trước đó đã xuống -> Giờ lên -> Tính 1 cái
                count++
                isDown = false // Reset trạng thái
                currentInstruction = "Tốt! Xuống tiếp"
            } else {
                currentInstruction = "Xuống thấp nữa"
            }
        }

        return count
    }

    override fun getInstruction(): String {
        return currentInstruction
    }

    override fun reset() {
        count = 0
        isDown = false
        currentInstruction = "Sẵn sàng"
    }
}