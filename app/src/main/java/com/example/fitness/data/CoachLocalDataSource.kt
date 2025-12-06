package com.example.fitness.data

import android.content.Context
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json



class CoachLocalDataSource(
    private val context: Context
) {

    private val json by lazy {
        Json { ignoreUnknownKeys = true }
    }

    private val cache by lazy {
        loadFromAssets()
    }

    private fun loadFromAssets(): List<CoachLocalAnswer> {
        return try {
            val input = context.assets.open("coach_dataset.json")
            val text = input.bufferedReader().use { it.readText() }
            json.decodeFromString(
                ListSerializer(CoachLocalAnswer.serializer()),
                text
            )
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Tìm câu trả lời phù hợp nhất theo câu hỏi người dùng
     */
    fun findBestAnswer(question: String): String? {
        val q = question.lowercase()

        val hit = cache.firstOrNull { item ->
            item.patterns.any { p ->
                q.contains(p.lowercase())
            }
        }

        return hit?.response
    }
}

