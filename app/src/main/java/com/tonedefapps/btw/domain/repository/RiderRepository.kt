package com.tonedefapps.btw.domain.repository

import com.tonedefapps.btw.domain.model.Rider
import kotlinx.coroutines.flow.Flow

interface RiderRepository {
    fun getRiders(): Flow<List<Rider>>
    suspend fun getRiderById(id: Long): Rider?
    suspend fun addRider(rider: Rider): Long
    suspend fun updateRider(rider: Rider)
    suspend fun deleteRider(id: Long)
    suspend fun pauseRider(id: Long, until: Long)
    suspend fun unpauseRider(id: Long)
}
