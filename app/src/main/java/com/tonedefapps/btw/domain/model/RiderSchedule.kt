package com.tonedefapps.btw.domain.model

import java.util.Calendar

enum class ScheduleType { WEEKLY, ALTERNATING_WEEKS }

data class RiderSchedule(
    val riderId: Long,
    val type: ScheduleType,
    val daysOfWeekBitmask: Int = 0,
    val referenceDate: Long = 0L
)

fun RiderSchedule.isActiveOn(epochMs: Long): Boolean = when (type) {
    ScheduleType.WEEKLY -> {
        val dayOfWeek = Calendar.getInstance().apply { timeInMillis = epochMs }
            .get(Calendar.DAY_OF_WEEK)
        daysOfWeekBitmask and (1 shl dayOfWeek) != 0
    }
    ScheduleType.ALTERNATING_WEEKS -> {
        val msPerWeek = 7L * 24 * 60 * 60 * 1000
        val weeksDiff = ((epochMs - referenceDate) / msPerWeek).toInt()
        ((weeksDiff % 2) + 2) % 2 == 0
    }
}

fun Rider.isActive(schedule: RiderSchedule?, now: Long = System.currentTimeMillis()): Boolean {
    if (schedule == null) return true
    return schedule.isActiveOn(now)
}
