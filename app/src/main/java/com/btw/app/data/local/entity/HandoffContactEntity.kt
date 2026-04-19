package com.btw.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.btw.app.domain.model.HandoffContact

@Entity(tableName = "handoff_contacts")
data class HandoffContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val riderId: Long,
    val name: String,
    val phone: String
) {
    fun toDomain() = HandoffContact(id = id, riderId = riderId, name = name, phone = phone)

    companion object {
        fun fromDomain(c: HandoffContact) = HandoffContactEntity(
            id = c.id,
            riderId = c.riderId,
            name = c.name,
            phone = c.phone
        )
    }
}
