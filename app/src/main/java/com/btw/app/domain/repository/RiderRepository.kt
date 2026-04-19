package com.btw.app.domain.repository

import com.btw.app.domain.model.Rider
import kotlinx.coroutines.flow.Flow

interface RiderRepository {
    fun getRiders(): Flow<List<Rider>>
    suspend fun getRiderById(id: Long): Rider?
    suspend fun addRider(rider: Rider): Long
    suspend fun updateRider(rider: Rider)
    suspend fun deleteRider(id: Long)
}
