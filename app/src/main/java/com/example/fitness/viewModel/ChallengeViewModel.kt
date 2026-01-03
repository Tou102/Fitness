package com.example.fitness.viewModel

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitness.dao.ChallengeDao
import com.example.fitness.data.ChallengeKeys
import com.example.fitness.data.challengeDataStore
import com.example.fitness.entity.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChallengeViewModel(private val challengeDao: ChallengeDao) : ViewModel() {

    private val _activeChallenge = MutableStateFlow<RunningChallengeEntity?>(null)
    val activeChallenge: StateFlow<RunningChallengeEntity?> = _activeChallenge.asStateFlow()

    private val _todayGoal = MutableStateFlow<DailyGoalEntity?>(null)
    val todayGoal: StateFlow<DailyGoalEntity?> = _todayGoal.asStateFlow()

    private val _completedDays = MutableStateFlow(0)
    val completedDays: StateFlow<Int> = _completedDays.asStateFlow()

    init {
        reloadActiveChallenge()
    }

    fun reloadActiveChallenge() {
        viewModelScope.launch {
            try {
                val challenge = challengeDao.getActiveChallenge()
                _activeChallenge.value = challenge
                if (challenge != null) {
                    val dayNumber = getCurrentDayNumber(challenge)
                    _todayGoal.value = challengeDao.getGoalForDay(challenge.id, dayNumber)
                    _completedDays.value = challengeDao.getCompletedDaysCount(challenge.id)
                    Log.d("ChallengeVM", "Loaded active challenge: ${challenge.name} (days: $dayNumber)")
                } else {
                    _todayGoal.value = null
                    _completedDays.value = 0
                    Log.d("ChallengeVM", "No active challenge found")
                }
            } catch (e: Exception) {
                Log.e("ChallengeVM", "Failed to load active challenge", e)
            }
        }
    }

    fun getCurrentDayNumber(challenge: RunningChallengeEntity): Int {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        val start = challenge.startDate
        val diff = today - start
        return if (diff <= 0) 1 else ((diff / (1000 * 60 * 60 * 24)) + 1).toInt().coerceAtMost(challenge.totalDays)
    }

    fun createNewChallenge(
        name: String,
        totalDays: Int,
        template: String,
        context: Context
    ) {
        viewModelScope.launch {
            val challengeId = UUID.randomUUID().toString()
            val startDate = System.currentTimeMillis()

            val challenge = RunningChallengeEntity(
                id = challengeId,
                name = name,
                totalDays = totalDays,
                startDate = startDate,
                isActive = true
            )
            challengeDao.insertChallenge(challenge)

            val goals = generateGoals(challengeId, totalDays, template)
            challengeDao.insertDailyGoals(goals)

            // Set flag khóa app ngay lập tức
            context.challengeDataStore.edit { prefs ->
                prefs[ChallengeKeys.IS_CHALLENGE_ACTIVE] = true
            }

            reloadActiveChallenge()
            Log.d("ChallengeVM", "New challenge created: $name, locked via DataStore")
        }
    }

    private fun generateGoals(challengeId: String, totalDays: Int, template: String): List<DailyGoalEntity> {
        val goals = mutableListOf<DailyGoalEntity>()

        // Ngày đầu tiên: LUÔN là chạy bộ 3km (khởi động)
        goals.add(DailyGoalEntity(
            challengeId = challengeId,
            dayNumber = 1,
            targetDistanceMeters = 3000f,
            description = "Ngày 1: Chạy bộ khởi động 3km"
        ))

        when (template) {
            "gain_muscle_fat_loss" -> {
                // Tăng cơ - Giảm mỡ: HIIT + bodyweight, xen kẽ chạy nhẹ
                for (day in 2..totalDays) {
                    val week = (day - 1) / 7 + 1
                    val isRest = day % 7 == 0
                    if (isRest) {
                        goals.add(DailyGoalEntity(challengeId = challengeId, dayNumber = day, description = "Nghỉ phục hồi", isRestDay = true))
                    } else {
                        val distanceKm = (2.0 + (week - 1) * 0.3).coerceAtMost(5.0)
                        val pushups = 10 + week * 2
                        val squats = 20 + week * 3
                        val plankSec = 30 + week * 10
                        val burpees = 5 + week
                        goals.add(DailyGoalEntity(
                            challengeId = challengeId,
                            dayNumber = day,
                            targetDistanceMeters = (distanceKm * 1000).toFloat(),
                            description = "HIIT: Chạy ${"%.1f".format(distanceKm)}km + hít đất ${pushups} cái + squat ${squats} cái + plank ${plankSec}s + burpees ${burpees} cái"
                        ))
                    }
                }
            }

            "gain_weight" -> {
                // Tăng cân: Tập nặng bodyweight + calo surplus, chạy nhẹ giữ tim mạch
                for (day in 2..totalDays) {
                    val week = (day - 1) / 7 + 1
                    val isRest = day % 7 == 6
                    if (isRest) {
                        goals.add(DailyGoalEntity(challengeId = challengeId, dayNumber = day, description = "Nghỉ + ăn surplus calo", isRestDay = true))
                    } else {
                        val squats = 30 + week * 5
                        val pushups = 15 + week * 3
                        val deadliftReps = 10 + week * 2
                        goals.add(DailyGoalEntity(
                            challengeId = challengeId,
                            dayNumber = day,
                            description = "Tập nặng: Squat ${squats} cái + hít đất ${pushups} cái + deadlift bodyweight ${deadliftReps}x3 + chạy nhẹ 2km"
                        ))
                    }
                }
            }

            "lose_weight" -> {
                // Giảm cân: Cardio + chạy mạnh
                for (day in 2..totalDays) {
                    val week = (day - 1) / 7 + 1
                    val distanceKm = 3.0 + (week - 1) * 0.5
                    val isRest = day % 7 == 0
                    if (isRest) {
                        goals.add(DailyGoalEntity(challengeId = challengeId, dayNumber = day, description = "Nghỉ phục hồi", isRestDay = true))
                    } else {
                        goals.add(DailyGoalEntity(
                            challengeId = challengeId,
                            dayNumber = day,
                            targetDistanceMeters = (distanceKm * 1000).toFloat(),
                            description = "Cardio: Chạy ${"%.1f".format(distanceKm)}km + interval (30s nhanh - 1p chậm) + nhảy dây 10p"
                        ))
                    }
                }
            }

            "custom" -> {
                // Tùy chỉnh: Tạm fallback hoặc dựa vào AI (sau này parse từ aiSuggestion)
                for (day in 2..totalDays) {
                    goals.add(DailyGoalEntity(
                        challengeId = challengeId,
                        dayNumber = day,
                        description = "Bài tập tùy chỉnh (AI gợi ý)"
                    ))
                }
            }

            else -> {
                // Fallback nếu template lạ
                for (day in 2..totalDays) {
                    goals.add(DailyGoalEntity(
                        challengeId = challengeId,
                        dayNumber = day,
                        targetDistanceMeters = 3000f,
                        description = "Chạy 3km (mặc định)"
                    ))
                }
            }
        }
        return goals
    }

    fun endChallengeEarly(context: Context) {
        viewModelScope.launch {
            val challenge = _activeChallenge.value ?: return@launch
            challengeDao.finishChallenge(challenge.id, completed = false)

            // Mở khóa flag DataStore
            context.challengeDataStore.edit { prefs ->
                prefs[ChallengeKeys.IS_CHALLENGE_ACTIVE] = false
            }

            reloadActiveChallenge()
            Log.d("ChallengeVM", "Challenge ended early, unlocked via DataStore")
        }
    }

    fun getRemainingTime(): String {
        val challenge = _activeChallenge.value ?: return "Không có thử thách"
        val endDate = challenge.startDate + (challenge.totalDays * 86_400_000L)
        val remaining = endDate - System.currentTimeMillis()
        if (remaining <= 0) return "Đã kết thúc"

        val days = remaining / 86_400_000L
        val hours = (remaining % 86_400_000L) / 3_600_000L
        val minutes = (remaining % 3_600_000L) / 60_000L

        return when {
            days > 0 -> "Còn $days ngày $hours giờ"
            hours > 0 -> "Còn $hours giờ $minutes phút"
            else -> "Còn $minutes phút"
        }
    }

    companion object {
        fun factory(challengeDao: ChallengeDao): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChallengeViewModel(challengeDao) as T
            }
        }
    }
}