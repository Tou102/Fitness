package com.example.fitness.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


import kotlinx.coroutines.delay

@Composable
fun AIWorkoutScreen(
    navController: NavController,
    level: DifficultyLevel = DifficultyLevel.EASY,
    viewModel: AIWorkoutViewModel = viewModel()
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )

    // Khởi tạo AI khi có quyền và màn hình được tạo
    LaunchedEffect(key1 = hasPermission) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            viewModel.initialize(context, level)
        }
    }

    // Logic tự động chuyển bài (lắng nghe state instruction)
    LaunchedEffect(key1 = viewModel.instruction) {
        if (viewModel.instruction.contains("Hoàn thành")) {
            delay(1500) // Đợi 1.5s
            viewModel.nextExercise()
        }
        if (viewModel.instruction.contains("HOÀN TẤT")) {
            delay(2000)
            navController.popBackStack()
        }
    }

    if (hasPermission) {
        val currentExercise = viewModel.workoutSchedule.getOrElse(viewModel.currentExerciseIndex) {
            viewModel.workoutSchedule.firstOrNull()
        }

        if (currentExercise != null) {
            // Gọi lại CameraScreen
            CameraScreen(
                poseResult = viewModel.poseResult,
                inputImageWidth = viewModel.inputImageWidth,
                inputImageHeight = viewModel.inputImageHeight,
                exerciseName = currentExercise.name,
                currentCount = viewModel.repCount,
                targetValue = currentExercise.targetValue,
                unit = currentExercise.unit,
                instruction = viewModel.instruction,
                cameraExecutor = viewModel.cameraExecutor,
                onBackClick = { navController.popBackStack() },
                onImageAnalysis = { imageProxy -> viewModel.detectPose(imageProxy) }
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Đang tải dữ liệu bài tập...")
            }
        }
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cần cấp quyền Camera để tập luyện")
        }
    }
}