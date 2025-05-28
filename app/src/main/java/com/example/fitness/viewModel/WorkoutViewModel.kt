package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.dao.WorkoutSessionDao
import com.example.fitness.entity.WorkoutSession
import com.example.fitness.getWeekStartEndTimestamps
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class WorkoutViewModel(private val workoutDao: WorkoutSessionDao) : ViewModel() {
    private val _weeklySessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val weeklySessions: StateFlow<List<WorkoutSession>> = _weeklySessions.asStateFlow()

    fun loadWeeklySessions(userId: Int) {
        viewModelScope.launch {
            val (startOfWeek, endOfWeek) = getWeekStartEndTimestamps()
            _weeklySessions.value = workoutDao.getSessionsInPeriod(userId, startOfWeek, endOfWeek)
        }
    }

    fun checkInWorkout(userId: Int, day: Int, workoutType: String) {
        viewModelScope.launch {
            // Tạo timestamp cho ngày bắt đầu của ngày đó trong tuần hiện tại
            val calendar = Calendar.getInstance()
            // Xác định ngày hiện tại của tuần (Calendar.MONDAY=2, ... Calendar.SUNDAY=1)
            // Cài đặt calendar về ngày bắt đầu tuần (thứ 2)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY + (day - 1))
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val dateTimestamp = calendar.timeInMillis

            val newSession = WorkoutSession(
                userId = userId,
                date = dateTimestamp,   // ngày đúng tương ứng với ngày trong tuần
                day = day,
                workoutType = workoutType,
                completed = true
            )
            workoutDao.insertSession(newSession)

            // Tải lại lịch tập tuần mới sau khi insert
            loadWeeklySessions(userId)
        }
    }
}
