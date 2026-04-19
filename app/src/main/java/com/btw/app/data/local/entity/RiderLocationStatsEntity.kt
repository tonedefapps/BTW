package com.btw.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.btw.app.domain.model.RiderLocationStats

@Entity(
    tableName = "rider_location_stats",
    indices = [Index(value = ["riderId", "locationId"], unique = true)]
)
data class RiderLocationStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val riderId: Long,
    val locationId: Long,
    val presentCount: Int = 0,
    val absentCount: Int = 0
) {
    fun toDomain() = RiderLocationStats(
        id = id,
        riderId = riderId,
        locationId = locationId,
        presentCount = presentCount,
        absentCount = absentCount
    )

    companion object {
        fun fromDomain(stats: RiderLocationStats) = RiderLocationStatsEntity(
            id = stats.id,
            riderId = stats.riderId,
            locationId = stats.locationId,
            presentCount = stats.presentCount,
            absentCount = stats.absentCount
        )
    }
}
