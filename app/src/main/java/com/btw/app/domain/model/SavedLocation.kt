package com.btw.app.domain.model

data class SavedLocation(
    val id: Long = 0,
    val lat: Double,
    val lng: Double,
    val radiusMeters: Float = 50f,
    val label: String,
    val emoji: String = "",
    val source: LocationSource,
    val confidence: Float,
    val visitCount: Int = 0,
    val lastVisited: Long? = null
) {
    fun updatedAfterVisit(): SavedLocation {
        val newVisitCount = visitCount + 1
        val newConfidence = when (source) {
            LocationSource.MANUAL -> 1.0f
            LocationSource.LEARNED -> minOf(1.0f, newVisitCount * 0.1f)
        }
        return copy(
            visitCount = newVisitCount,
            confidence = newConfidence,
            lastVisited = System.currentTimeMillis()
        )
    }
}
