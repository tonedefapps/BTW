package com.tonedefapps.btw.data.local.entity

import androidx.room.*
import com.tonedefapps.btw.domain.model.RiderSchedule
import com.tonedefapps.btw.domain.model.ScheduleType

@Entity(
    tableName = "rider_schedules",
    foreignKeys = [ForeignKey(
        entity = RiderEntity::class,
        parentColumns = ["id"],
        childColumns = ["riderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("riderId")]
)
data class RiderScheduleEntity(
    @PrimaryKey val riderId: Long,
    val type: String,
    val daysOfWeekBitmask: Int = 0,
    val referenceDate: Long = 0L
) {
    fun toDomain() = RiderSchedule(
        riderId = riderId,
        type = ScheduleType.valueOf(type),
        daysOfWeekBitmask = daysOfWeekBitmask,
        referenceDate = referenceDate
    )

    companion object {
        fun fromDomain(s: RiderSchedule) = RiderScheduleEntity(
            riderId = s.riderId,
            type = s.type.name,
            daysOfWeekBitmask = s.daysOfWeekBitmask,
            referenceDate = s.referenceDate
        )
    }
}
