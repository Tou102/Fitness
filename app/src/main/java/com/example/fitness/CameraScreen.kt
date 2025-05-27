package com.example.fitness

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.pose.PoseLandmark

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var pushUpCount by remember { mutableStateOf(0) }
    val poseLandmarks = remember { mutableStateOf<List<PoseLandmark>>(emptyList()) }

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (!cameraPermissionState.status.isGranted) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Yêu cầu quyền camera để bắt đầu")
        }
        return
    }

    val previewView = remember { PreviewView(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = "Số rep: $pushUpCount",
            fontSize = 32.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        PoseOverlay(
            modifier = Modifier.fillMaxSize(),
            landmarks = poseLandmarks.value
        )
    }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context), PoseAnalyzer(
                    onPushUpDetected = { isPushUp ->
                        if (isPushUp) pushUpCount++
                    },
                    onPoseUpdated = { poseLandmarks.value = it }
                ))
            }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun PoseOverlay(modifier: Modifier = Modifier, landmarks: List<PoseLandmark>) {
    Canvas(modifier = modifier) {
        landmarks.forEach { landmark ->
            drawCircle(
                color = Color.Green,
                radius = 10f,
                center = Offset(landmark.position.x, landmark.position.y)
            )
        }

        fun drawBone(type1: Int, type2: Int) {
            val p1 = landmarks.find { it.landmarkType == type1 }
            val p2 = landmarks.find { it.landmarkType == type2 }
            if (p1 != null && p2 != null) {
                drawLine(
                    color = Color.Yellow,
                    strokeWidth = 4f,
                    start = Offset(p1.position.x, p1.position.y),
                    end = Offset(p2.position.x, p2.position.y)
                )
            }
        }

        drawBone(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW)
        drawBone(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST)
        drawBone(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW)
        drawBone(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST)
        drawBone(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP)
        drawBone(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP)
        drawBone(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE)
        drawBone(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)
    }
}
