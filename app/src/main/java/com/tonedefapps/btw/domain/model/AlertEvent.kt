package com.tonedefapps.btw.domain.model

data class AlertEvent(
    val id: Long = 0,
    val riderId: Long,
    val riderName: String,
    val vehicleId: Long,
    val vehicleName: String,
    val triggeredAt: Long = System.currentTimeMillis(),
    val acknowledgedAt: Long? = null,
    val outcome: AlertOutcome = AlertOutcome.PENDING,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

enum class AlertOutcome { PENDING, SAFE, WENT_BACK, ESCALATED_SMS, DISMISSED }
