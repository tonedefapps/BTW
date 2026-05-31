package com.tonedefapps.btw.data.repository

import com.tonedefapps.btw.data.local.dao.RiderScheduleDao
import com.tonedefapps.btw.data.local.entity.RiderScheduleEntity
import com.tonedefapps.btw.domain.model.RiderSchedule
import com.tonedefapps.btw.domain.repository.RiderScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RiderScheduleRepositoryImpl @Inject constructor(
    private val dao: RiderScheduleDao
) : RiderScheduleRepository {

    override fun getScheduleForRider(riderId: Long): Flow<RiderSchedule?> =
        dao.getScheduleForRider(riderId).map { it?.toDomain() }

    override fun getSchedules(): Flow<List<RiderSchedule>> =
        dao.getAllSchedules().map { list -> list.map { it.toDomain() } }

    override suspend fun upsertSchedule(schedule: RiderSchedule) =
        dao.upsert(RiderScheduleEntity.fromDomain(schedule))

    override suspend fun deleteSchedule(riderId: Long) =
        dao.deleteForRider(riderId)

    override suspend fun getAllSchedulesList(): List<RiderSchedule> =
        dao.getAllSchedulesList().map { it.toDomain() }
}
