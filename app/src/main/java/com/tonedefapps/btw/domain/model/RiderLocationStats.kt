package com.tonedefapps.btw.domain.model

data class RiderLocationStats(
    val id: Long = 0,
    val riderId: Long,
    val locationId: Long,
    val presentCount: Int = 0,
    val absentCount: Int = 0
)
