package com.btw.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.btw.app.domain.model.AlertEvent
import com.btw.app.domain.model.AlertOutcome

@Entity(tableName = "alert_events")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val riderId: Long,
    val riderName: String,
    val vehicleId: Long,
    val vehicleName: String,
    val triggeredAt: Long,
    val acknowledgedAt: Long?,
    val outcome: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toDomain() = AlertEvent(
        id = id,
        riderId = riderId,
        riderName = riderName,
        vehicleId = vehicleId,
        vehicleName = vehicleName,
        triggeredAt = triggeredAt,
        acknowledgedAt = acknowledgedAt,
        outcome = AlertOutcome.valueOf(outcome),
        latitude = latitude,
        longitude = longitude
    )

    companion object {
        fun fromDomain(alert: AlertEvent) = AlertEntity(
            id = alert.id,
            riderId = alert.riderId,
            riderName = alert.riderName,
            vehicleId = alert.vehicleId,
            vehicleName = alert.vehicleName,
            triggeredAt = alert.triggeredAt,
            acknowledgedAt = alert.acknowledgedAt,
            outcome = alert.outcome.name,
            latitude = alert.latitude,
            longitude = alert.longitude
        )
    }
}
