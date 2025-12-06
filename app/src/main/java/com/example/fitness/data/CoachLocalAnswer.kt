package com.example.fitness.data

import kotlinx.serialization.Serializable

@Serializable
data class CoachLocalAnswer(
    val patterns: List<String>,
    val response: String
)
