package com.example.fitness

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import java.lang.Math.toDegrees
import kotlin.math.abs
import kotlin.math.atan2


class PoseAnalyzer(
    private val onPushUpDetected: (Boolean) -> Unit,
    private val onPoseUpdated: (List<PoseLandmark>) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastPoseDown = false
    private var poseDownCounter = 0
    private val poseDownThreshold = 3

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()

        val poseDetector = PoseDetection.getClient(options)

        poseDetector.process(inputImage)
            .addOnSuccessListener { pose ->
                val landmarks = pose.allPoseLandmarks

                // Gửi landmarks ra ngoài để vẽ overlay
                if (landmarks.isNotEmpty()) {
                    onPoseUpdated(landmarks)
                }

                // Dùng vai, hông, gối để tính toán góc cho push-up
                val shoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                val hip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
                val knee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)

                if (shoulder != null && hip != null && knee != null) {
                    val angle = calculateAngle(shoulder, hip, knee)

                    if (angle < 90) {
                        poseDownCounter++
                        if (poseDownCounter >= poseDownThreshold) {
                            lastPoseDown = true
                        }
                    } else if (angle > 160 && lastPoseDown) {
                        onPushUpDetected(true)
                        lastPoseDown = false
                        poseDownCounter = 0
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun calculateAngle(p1: PoseLandmark, p2: PoseLandmark, p3: PoseLandmark): Double {
        val radians = atan2(
            (p3.position.y - p2.position.y).toDouble(),
            (p3.position.x - p2.position.x).toDouble()
        ) - atan2(
            (p1.position.y - p2.position.y).toDouble(),
            (p1.position.x - p2.position.x).toDouble()
        )
        val angle = abs(toDegrees(radians))
        return if (angle > 180) 360 - angle else angle
    }
}
