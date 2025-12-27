package com.example.fitness.camera


import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    poseResult: PoseLandmarkerResult?, // Dữ liệu từ MainActivity truyền vào
    inputImageWidth: Int,    // ← thêm
    inputImageHeight: Int,   // ← thêm
    exerciseName: String,
    currentCount: Int,
    targetValue: Int,
    unit: String,
    instruction: String,
    cameraExecutor: Executor,
    onBackClick: () -> Unit,
    onImageAnalysis: (androidx.camera.core.ImageProxy) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(modifier = Modifier.fillMaxSize()) {
        // --- 1. CAMERA (Lớp dưới cùng) ---
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            update = { previewView ->
                setupCamera(context, lifecycleOwner, previewView, cameraExecutor, onImageAnalysis)
            }
        )

        // --- 2. VẼ XƯƠNG (Gọi từ file PoseOverlay.kt) ---
        // Android Studio sẽ tự Import file PoseOverlay của bạn
        PoseOverlay(
            result = poseResult,
            inputImageWidth = inputImageWidth,    // truyền tiếp
            inputImageHeight = inputImageHeight,  // truyền tiếp
            modifier = Modifier.fillMaxSize()
        )

        // --- 3. GIAO DIỆN THÔNG TIN (Lớp trên cùng) ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tên bài tập
            Text(
                text = exerciseName.uppercase(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )

            // Số đếm
            Text(
                text = "$currentCount / $targetValue $unit",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Hướng dẫn
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = instruction,
                    fontSize = 24.sp,
                    color = Color.Green,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Nút Back
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(50))
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
    }
}

// Hàm setupCamera (Camera SAU)
private fun setupCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    cameraExecutor: Executor,
    analyzer: (androidx.camera.core.ImageProxy) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        // Camera SAU
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("CameraScreen", "Lỗi khởi tạo Camera", e)
        }
    }, ContextCompat.getMainExecutor(context))
}