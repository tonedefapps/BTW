package com.tonedefapps.btw.domain.model

data class Rider(
    val id: Long = 0,
    val name: String,
    val type: RiderType,
    val emoji: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
