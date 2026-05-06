package com.tonedefapps.btw.domain.repository

import com.tonedefapps.btw.domain.model.HandoffContact
import com.tonedefapps.btw.domain.model.HandoffEvent
import com.tonedefapps.btw.domain.model.HandoffOutcome
import com.tonedefapps.btw.domain.model.PickupWindow
import kotlinx.coroutines.flow.Flow

interface HandoffRepository {
    // Contacts
    fun getContactsForRider(riderId: Long): Flow<List<HandoffContact>>
    suspend fun getAllContactsForRider(riderId: Long): List<HandoffContact>
    suspend fun addContact(contact: HandoffContact): Long
    suspend fun deleteContact(id: Long)

    // Pickup windows
    fun getWindowsForRider(riderId: Long): Flow<List<PickupWindow>>
    suspend fun getActiveWindowsForLocation(locationId: Long): List<PickupWindow>
    suspend fun addWindow(window: PickupWindow): Long
    suspend fun updateWindow(window: PickupWindow)
    suspend fun deleteWindow(id: Long)

    // Handoff events
    fun getHandoffHistory(): Flow<List<HandoffEvent>>
    suspend fun insertHandoffEvent(event: HandoffEvent): Long
    suspend fun updateHandoffOutcome(id: Long, outcome: HandoffOutcome, verifiedBy: String? = null)
    suspend fun getPendingHandoffForRider(riderId: Long): HandoffEvent?
    suspend fun confirmBySmsPhone(phone: String): Boolean
}
