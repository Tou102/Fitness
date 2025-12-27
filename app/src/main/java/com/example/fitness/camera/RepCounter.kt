package com.example.fitness.camera

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark


interface RepCounter {
    fun process(landmarks: List<NormalizedLandmark>): Int
    fun getInstruction(): String // Ví dụ: "Xuống thấp nữa"
    fun reset()
}