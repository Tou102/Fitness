package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.dao.WorkoutSessionDao
import com.example.fitness.entity.WorkoutSession
import com.example.fitness.getWeekStartEndTimestamps
import kotlinx.coroutines.flow.Flow
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

    // --- ĐÃ SỬA: Thêm tham số duration và dùng System.currentTimeMillis() ---
    fun checkInWorkout(userId: Int, day: Int, workoutType: String, duration: Long = 0) {
        viewModelScope.launch {
            // SỬA LỖI 12:00 AM:
            // Không dùng Calendar set 00:00 nữa. Dùng giờ thực tế hiện tại.
            val currentTimestamp = System.currentTimeMillis()

            val newSession = WorkoutSession(
                userId = userId,
                date = currentTimestamp, // Lưu giờ phút thực tế (VD: 14:30)
                day = day,
                workoutType = workoutType,
                completed = true,
                duration = duration // Lưu thời gian tập
            )

            workoutDao.insertSession(newSession)

            // Tải lại lịch tập tuần mới sau khi insert
            loadWeeklySessions(userId)
        }
    }

    // Hàm này tự động tính xem hôm nay là thứ mấy (theo chuẩn T2=1 -> CN=7) rồi lưu
    fun markTodayCompleted(userId: Int, workoutType: String, durationInMillis: Long) {
        val calendar = Calendar.getInstance()
        // Trong Android: Sunday=1, Monday=2, ..., Saturday=7
        val androidDay = calendar.get(Calendar.DAY_OF_WEEK)

        // Chuyển đổi sang chuẩn của App bạn (Profile đang dùng T2 là index 0 -> day=1)
        val appDay = when (androidDay) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }

        // Truyền duration xuống hàm checkInWorkout ---
        checkInWorkout(userId, appDay, workoutType, durationInMillis)
    }

    fun getHistory(userId: Int): Flow<List<WorkoutSession>> {
        return workoutDao.getAllSessions(userId)
    }
}