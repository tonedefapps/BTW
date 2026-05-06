package com.tonedefapps.btw.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.model.RiderType

@Entity(tableName = "riders")
data class RiderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val emoji: String,
    val createdAt: Long
) {
    fun toDomain() = Rider(
        id = id,
        name = name,
        type = RiderType.valueOf(type),
        emoji = emoji,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(rider: Rider) = RiderEntity(
            id = rider.id,
            name = rider.name,
            type = rider.type.name,
            emoji = rider.emoji,
            createdAt = rider.createdAt
        )
    }
}
