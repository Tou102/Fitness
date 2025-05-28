package com.example.fitness

import java.util.Calendar

fun getWeekStartEndTimestamps(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    // Đặt thứ Hai là ngày đầu tuần (theo chuẩn ISO)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfWeek = calendar.timeInMillis

    calendar.add(Calendar.DAY_OF_WEEK, 6)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfWeek = calendar.timeInMillis

    return Pair(startOfWeek, endOfWeek)
}
