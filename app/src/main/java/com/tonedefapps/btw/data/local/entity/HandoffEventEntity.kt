package com.tonedefapps.btw.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tonedefapps.btw.domain.model.HandoffEvent
import com.tonedefapps.btw.domain.model.HandoffOutcome

@Entity(tableName = "handoff_events")
data class HandoffEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val riderId: Long,
    val locationId: Long,
    val riderName: String,
    val locationLabel: String,
    val expectedAt: Long,
    val occurredAt: Long?,
    val verifiedBy: String?,
    val outcome: String,
    val smsSentTo: String?,
    val createdAt: Long
) {
    fun toDomain() = HandoffEvent(
        id = id,
        riderId = riderId,
        locationId = locationId,
        riderName = riderName,
        locationLabel = locationLabel,
        expectedAt = expectedAt,
        occurredAt = occurredAt,
        verifiedBy = verifiedBy,
        outcome = HandoffOutcome.valueOf(outcome),
        smsSentTo = smsSentTo,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(e: HandoffEvent) = HandoffEventEntity(
            id = e.id,
            riderId = e.riderId,
            locationId = e.locationId,
            riderName = e.riderName,
            locationLabel = e.locationLabel,
            expectedAt = e.expectedAt,
            occurredAt = e.occurredAt,
            verifiedBy = e.verifiedBy,
            outcome = e.outcome.name,
            smsSentTo = e.smsSentTo,
            createdAt = e.createdAt
        )
    }
}
