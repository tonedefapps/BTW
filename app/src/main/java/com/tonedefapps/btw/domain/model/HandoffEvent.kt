package com.tonedefapps.btw.domain.model

data class HandoffEvent(
    val id: Long = 0,
    val riderId: Long,
    val locationId: Long,
    val riderName: String,
    val locationLabel: String,
    val expectedAt: Long,
    val occurredAt: Long? = null,
    val verifiedBy: String? = null,
    val outcome: HandoffOutcome = HandoffOutcome.PENDING,
    val smsSentTo: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class HandoffOutcome { PENDING, COMPLETED, MISSED, VERIFIED_SMS, ESCALATED }
