package com.example.fitness.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.Toast
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitness.camera.CounterFactory
import com.example.fitness.camera.RepCounter
import com.example.fitness.camera.WorkoutRepository
import com.example.fitness.camera.DifficultyLevel
import com.example.fitness.camera.ExerciseScheduleItem
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AIWorkoutViewModel : ViewModel() {

    // --- STATE UI ---
    var workoutSchedule = emptyList<ExerciseScheduleItem>()
    var currentExerciseIndex by mutableIntStateOf(0)
    var repCount by mutableIntStateOf(0)
    var instruction by mutableStateOf("Đang tải ...")

    var poseResult by mutableStateOf<PoseLandmarkerResult?>(null)
    var inputImageWidth by mutableIntStateOf(0)
    var inputImageHeight by mutableIntStateOf(0)

    // --- LOGIC ---
    private var isExerciseCompleted = false
    private var repCounter: RepCounter? = null
    private var poseLandmarker: PoseLandmarker? = null
    val cameraExecutor: Executor = Executors.newSingleThreadExecutor()


    fun initialize(context: Context, level: DifficultyLevel) {
        setupPoseLandmarker(context)
        startGame(level)
    }

    private fun startGame(level: DifficultyLevel) {
        workoutSchedule = WorkoutRepository.getWorkoutPlan(level)
        isExerciseCompleted = false
        currentExerciseIndex = 0
        resetState()

        if (workoutSchedule.isNotEmpty()) {
            val first = workoutSchedule[0]
            repCounter = CounterFactory.getCounter(first.type)
            instruction = "Vào vị trí: ${first.name}"
        }
    }

    private fun setupPoseLandmarker(context: Context) {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("pose_landmarker_full.task")
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ -> processResult(result) }
            .build()

        try {
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            e.printStackTrace()
            instruction = "Lỗi tải : ${e.message}"
        }
    }

    // Xử lý từng frame ảnh từ Camera
    fun detectPose(imageProxy: ImageProxy) {
        if (poseLandmarker != null) {
            val originalBitmap = imageProxy.toBitmap()
            if (originalBitmap != null) {
                val matrix = Matrix()
                matrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                val rotatedBitmap = Bitmap.createBitmap(
                    originalBitmap, 0, 0,
                    originalBitmap.width, originalBitmap.height,
                    matrix, true
                )

                // Cập nhật kích thước để vẽ Overlay
                inputImageWidth = rotatedBitmap.width
                inputImageHeight = rotatedBitmap.height

                val mpImage = BitmapImageBuilder(rotatedBitmap).build()
                poseLandmarker?.detectAsync(mpImage, System.currentTimeMillis())
            }
        }
        imageProxy.close()
    }

    private fun processResult(result: PoseLandmarkerResult) {
        poseResult = result
        if (result.landmarks().isNotEmpty()) {
            val firstPerson = result.landmarks()[0]
            repCounter?.let { counter ->
                val newCount = counter.process(firstPerson)
                val newInstruction = counter.getInstruction()


                val currentExercise = workoutSchedule.getOrNull(currentExerciseIndex)
                if (currentExercise != null) {
                    if (newCount >= currentExercise.targetValue && !isExerciseCompleted) {
                        isExerciseCompleted = true
                        repCount = newCount
                        instruction = "Hoàn thành!"
                        // Tự động chuyển bài (Logic delay nên xử lý ở UI hoặc Coroutine)
                    } else if (!isExerciseCompleted) {
                        repCount = newCount
                        instruction = newInstruction
                    }
                }
            }
        }
    }

    fun nextExercise() {
        currentExerciseIndex++
        if (currentExerciseIndex < workoutSchedule.size) {
            val next = workoutSchedule[currentExerciseIndex]
            repCounter = CounterFactory.getCounter(next.type)
            repCounter?.reset()
            resetState()
            instruction = "Chuẩn bị: ${next.name}"
            isExerciseCompleted = false
        } else {
            instruction = "HOÀN TẤT BÀI TẬP! 🏆"
            repCounter = null
        }
    }

    private fun resetState() {
        repCount = 0
        poseResult = null
    }

    // Dọn dẹp khi thoát màn hình
    override fun onCleared() {
        super.onCleared()
        poseLandmarker?.close()
        // Không shutdown cameraExecutor ở đây nếu muốn tái sử dụng,
        // nhưng an toàn nhất là shutdown để tránh leak.
        // (Trong trường hợp này executor gắn với Lifecycle của VM)
    }
}