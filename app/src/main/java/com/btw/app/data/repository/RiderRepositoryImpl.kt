package com.btw.app.data.repository

import com.btw.app.data.local.dao.RiderDao
import com.btw.app.data.local.entity.RiderEntity
import com.btw.app.domain.model.Rider
import com.btw.app.domain.repository.RiderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RiderRepositoryImpl @Inject constructor(
    private val dao: RiderDao
) : RiderRepository {

    override fun getRiders(): Flow<List<Rider>> =
        dao.getAllRiders().map { list -> list.map { it.toDomain() } }

    override suspend fun getRiderById(id: Long): Rider? =
        dao.getRiderById(id)?.toDomain()

    override suspend fun addRider(rider: Rider): Long =
        dao.insert(RiderEntity.fromDomain(rider))

    override suspend fun updateRider(rider: Rider) =
        dao.update(RiderEntity.fromDomain(rider))

    override suspend fun deleteRider(id: Long) =
        dao.deleteById(id)
}
