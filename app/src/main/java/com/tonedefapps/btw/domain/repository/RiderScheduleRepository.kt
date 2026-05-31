package com.tonedefapps.btw.domain.repository

import com.tonedefapps.btw.domain.model.RiderSchedule
import kotlinx.coroutines.flow.Flow

interface RiderScheduleRepository {
    fun getScheduleForRider(riderId: Long): Flow<RiderSchedule?>
    fun getSchedules(): Flow<List<RiderSchedule>>
    suspend fun upsertSchedule(schedule: RiderSchedule)
    suspend fun deleteSchedule(riderId: Long)
    suspend fun getAllSchedulesList(): List<RiderSchedule>
}
