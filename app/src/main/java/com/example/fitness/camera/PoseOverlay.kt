package com.example.fitness.camera

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

@Composable
fun PoseOverlay(
    result: PoseLandmarkerResult?,
    inputImageWidth: Int,
    inputImageHeight: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (result == null || result.landmarks().isEmpty() ||
            inputImageWidth <= 0 || inputImageHeight <= 0) {
            return@Canvas
        }

        val landmarks = result.landmarks()[0]

        val imageWidth = inputImageWidth.toFloat()
        val imageHeight = inputImageHeight.toFloat()
        val imageAspect = imageWidth / imageHeight

        val canvasWidth = size.width
        val canvasHeight = size.height
        val viewAspect = canvasWidth / canvasHeight

        val scale: Float
        val offsetX: Float
        val offsetY: Float

        if (viewAspect > imageAspect) {
            scale = canvasWidth / imageWidth
            val scaledH = imageHeight * scale
            offsetX = 0f
            offsetY = (canvasHeight - scaledH) / 2f
        } else {
            scale = canvasHeight / imageHeight
            val scaledW = imageWidth * scale
            offsetX = (canvasWidth - scaledW) / 2f
            offsetY = 0f
        }

        fun toOffset(x: Float, y: Float): Offset {
            return Offset(
                x * imageWidth * scale + offsetX,
                y * imageHeight * scale + offsetY
            )
        }

        // Vẽ connections
        val connections = listOf(
            11 to 12, 11 to 13, 13 to 15,
            12 to 14, 14 to 16, 11 to 23,
            12 to 24, 23 to 24, 23 to 25,
            25 to 27, 24 to 26, 26 to 28
        )

        connections.forEach { (start, end) ->
            val p1 = landmarks[start]
            val p2 = landmarks[end]
            drawLine(
                Color.Green,
                toOffset(p1.x(), p1.y()),
                toOffset(p2.x(), p2.y()),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
        }

        // Vẽ points
        val points = landmarks.map { toOffset(it.x(), it.y()) }
        drawPoints(
            points = points,
            pointMode = PointMode.Points,
            color = Color.Yellow,
            strokeWidth = 15f
        )
    }
}