package com.btw.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.btw.app.domain.model.LocationSource
import com.btw.app.domain.model.SavedLocation

@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lat: Double,
    val lng: Double,
    val radiusMeters: Float,
    val label: String,
    val emoji: String,
    val source: String,
    val confidence: Float,
    val visitCount: Int,
    val lastVisited: Long?
) {
    fun toDomain() = SavedLocation(
        id = id,
        lat = lat,
        lng = lng,
        radiusMeters = radiusMeters,
        label = label,
        emoji = emoji,
        source = LocationSource.valueOf(source),
        confidence = confidence,
        visitCount = visitCount,
        lastVisited = lastVisited
    )

    companion object {
        fun fromDomain(loc: SavedLocation) = SavedLocationEntity(
            id = loc.id,
            lat = loc.lat,
            lng = loc.lng,
            radiusMeters = loc.radiusMeters,
            label = loc.label,
            emoji = loc.emoji,
            source = loc.source.name,
            confidence = loc.confidence,
            visitCount = loc.visitCount,
            lastVisited = loc.lastVisited
        )
    }
}
