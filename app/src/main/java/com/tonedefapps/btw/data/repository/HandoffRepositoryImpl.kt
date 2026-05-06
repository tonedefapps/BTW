package com.tonedefapps.btw.data.repository

import com.tonedefapps.btw.data.local.dao.HandoffDao
import com.tonedefapps.btw.data.local.entity.HandoffContactEntity
import com.tonedefapps.btw.data.local.entity.HandoffEventEntity
import com.tonedefapps.btw.data.local.entity.PickupWindowEntity
import com.tonedefapps.btw.domain.model.*
import com.tonedefapps.btw.domain.repository.HandoffRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HandoffRepositoryImpl @Inject constructor(
    private val dao: HandoffDao
) : HandoffRepository {

    override fun getContactsForRider(riderId: Long): Flow<List<HandoffContact>> =
        dao.getContactsForRider(riderId).map { it.map(HandoffContactEntity::toDomain) }

    override suspend fun getAllContactsForRider(riderId: Long): List<HandoffContact> =
        dao.getContactsListForRider(riderId).map { it.toDomain() }

    override suspend fun addContact(contact: HandoffContact): Long =
        dao.insertContact(HandoffContactEntity.fromDomain(contact))

    override suspend fun deleteContact(id: Long) = dao.deleteContact(id)

    override fun getWindowsForRider(riderId: Long): Flow<List<PickupWindow>> =
        dao.getWindowsForRider(riderId).map { it.map(PickupWindowEntity::toDomain) }

    override suspend fun getActiveWindowsForLocation(locationId: Long): List<PickupWindow> =
        dao.getActiveWindowsForLocation(locationId).map { it.toDomain() }

    override suspend fun addWindow(window: PickupWindow): Long =
        dao.insertWindow(PickupWindowEntity.fromDomain(window))

    override suspend fun updateWindow(window: PickupWindow) =
        dao.updateWindow(PickupWindowEntity.fromDomain(window))

    override suspend fun deleteWindow(id: Long) = dao.deleteWindow(id)

    override fun getHandoffHistory(): Flow<List<HandoffEvent>> =
        dao.getAllHandoffEvents().map { it.map(HandoffEventEntity::toDomain) }

    override suspend fun insertHandoffEvent(event: HandoffEvent): Long =
        dao.insertEvent(HandoffEventEntity.fromDomain(event))

    override suspend fun updateHandoffOutcome(id: Long, outcome: HandoffOutcome, verifiedBy: String?) =
        dao.updateOutcome(id, outcome.name, verifiedBy)

    override suspend fun getPendingHandoffForRider(riderId: Long): HandoffEvent? =
        dao.getPendingForRider(riderId)?.toDomain()

    override suspend fun confirmBySmsPhone(phone: String): Boolean {
        val event = dao.getPendingEventForContactPhone(phone) ?: return false
        dao.updateOutcome(event.id, HandoffOutcome.VERIFIED_SMS.name, phone)
        return true
    }
}
