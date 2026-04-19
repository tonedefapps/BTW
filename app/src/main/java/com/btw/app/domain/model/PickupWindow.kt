package com.btw.app.domain.model

import java.util.Calendar

/**
 * Defines when a rider is expected to be picked up at a given location.
 * daysOfWeek: Set of Calendar.SUNDAY (1) through Calendar.SATURDAY (7).
 * windowMinutes: tolerance around startHour:startMinute before declaring a missed pickup.
 */
data class PickupWindow(
    val id: Long = 0,
    val riderId: Long,
    val locationId: Long,
    val daysOfWeek: Set<Int>,
    val startHour: Int,
    val startMinute: Int,
    val windowMinutes: Int = 30,
    val isActive: Boolean = true
) {
    fun isActiveNow(cal: Calendar = Calendar.getInstance()): Boolean {
        if (!isActive) return false
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek !in daysOfWeek) return false
        val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val windowStart = startHour * 60 + startMinute
        return nowMinutes in windowStart..(windowStart + windowMinutes)
    }

    fun minutesUntilNextOccurrence(cal: Calendar = Calendar.getInstance()): Long {
        if (daysOfWeek.isEmpty()) return -1
        val targetMinutesInDay = startHour * 60 + startMinute
        var checkCal = cal.clone() as Calendar
        repeat(8) {
            val dayOfWeek = checkCal.get(Calendar.DAY_OF_WEEK)
            val nowMinutes = checkCal.get(Calendar.HOUR_OF_DAY) * 60 + checkCal.get(Calendar.MINUTE)
            if (dayOfWeek in daysOfWeek && targetMinutesInDay > nowMinutes) {
                return (targetMinutesInDay - nowMinutes).toLong()
            }
            checkCal.add(Calendar.DAY_OF_YEAR, 1)
            checkCal.set(Calendar.HOUR_OF_DAY, 0)
            checkCal.set(Calendar.MINUTE, 0)
        }
        return -1
    }
}
