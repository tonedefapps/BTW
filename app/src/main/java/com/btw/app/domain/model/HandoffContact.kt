package com.btw.app.domain.model

data class HandoffContact(
    val id: Long = 0,
    val riderId: Long,
    val name: String,
    val phone: String
)
