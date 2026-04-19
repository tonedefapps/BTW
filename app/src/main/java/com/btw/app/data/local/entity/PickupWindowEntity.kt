package com.btw.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.btw.app.domain.model.PickupWindow

@Entity(tableName = "pickup_windows")
data class PickupWindowEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val riderId: Long,
    val locationId: Long,
    val daysOfWeekBitmask: Int,  // bit 1=Sunday … bit 7=Saturday (Calendar constants)
    val startHour: Int,
    val startMinute: Int,
    val windowMinutes: Int,
    val isActive: Boolean
) {
    fun toDomain() = PickupWindow(
        id = id,
        riderId = riderId,
        locationId = locationId,
        daysOfWeek = bitmaskToDays(daysOfWeekBitmask),
        startHour = startHour,
        startMinute = startMinute,
        windowMinutes = windowMinutes,
        isActive = isActive
    )

    companion object {
        fun fromDomain(w: PickupWindow) = PickupWindowEntity(
            id = w.id,
            riderId = w.riderId,
            locationId = w.locationId,
            daysOfWeekBitmask = daysToBitmask(w.daysOfWeek),
            startHour = w.startHour,
            startMinute = w.startMinute,
            windowMinutes = w.windowMinutes,
            isActive = w.isActive
        )

        private fun daysToBitmask(days: Set<Int>): Int = days.fold(0) { acc, d -> acc or (1 shl d) }
        private fun bitmaskToDays(mask: Int): Set<Int> = (1..7).filter { mask and (1 shl it) != 0 }.toSet()
    }
}
