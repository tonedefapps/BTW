package com.tonedefapps.btw.domain.model

data class Rider(
    val id: Long = 0,
    val name: String,
    val type: RiderType,
    val emoji: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val pausedUntil: Long? = null
)

fun Rider.isManuallyPaused(now: Long = System.currentTimeMillis()): Boolean =
    pausedUntil != null && pausedUntil > now
