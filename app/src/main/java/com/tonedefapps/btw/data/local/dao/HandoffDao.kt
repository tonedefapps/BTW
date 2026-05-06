package com.tonedefapps.btw.data.local.dao

import androidx.room.*
import com.tonedefapps.btw.data.local.entity.HandoffContactEntity
import com.tonedefapps.btw.data.local.entity.HandoffEventEntity
import com.tonedefapps.btw.data.local.entity.PickupWindowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HandoffDao {

    // Contacts
    @Query("SELECT * FROM handoff_contacts WHERE riderId = :riderId ORDER BY name ASC")
    fun getContactsForRider(riderId: Long): Flow<List<HandoffContactEntity>>

    @Query("SELECT * FROM handoff_contacts WHERE riderId = :riderId ORDER BY name ASC")
    suspend fun getContactsListForRider(riderId: Long): List<HandoffContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: HandoffContactEntity): Long

    @Query("DELETE FROM handoff_contacts WHERE id = :id")
    suspend fun deleteContact(id: Long)

    // Pickup windows
    @Query("SELECT * FROM pickup_windows WHERE riderId = :riderId ORDER BY startHour ASC, startMinute ASC")
    fun getWindowsForRider(riderId: Long): Flow<List<PickupWindowEntity>>

    @Query("SELECT * FROM pickup_windows WHERE locationId = :locationId AND isActive = 1")
    suspend fun getActiveWindowsForLocation(locationId: Long): List<PickupWindowEntity>

    @Query("SELECT * FROM pickup_windows WHERE isActive = 1")
    suspend fun getAllActiveWindows(): List<PickupWindowEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWindow(window: PickupWindowEntity): Long

    @Update
    suspend fun updateWindow(window: PickupWindowEntity)

    @Query("DELETE FROM pickup_windows WHERE id = :id")
    suspend fun deleteWindow(id: Long)

    // Handoff events
    @Query("SELECT * FROM handoff_events ORDER BY createdAt DESC")
    fun getAllHandoffEvents(): Flow<List<HandoffEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: HandoffEventEntity): Long

    @Query("UPDATE handoff_events SET outcome = :outcome, verifiedBy = :verifiedBy WHERE id = :id")
    suspend fun updateOutcome(id: Long, outcome: String, verifiedBy: String?)

    @Query("""
        SELECT * FROM handoff_events
        WHERE riderId = :riderId AND outcome = 'PENDING'
        ORDER BY createdAt DESC LIMIT 1
    """)
    suspend fun getPendingForRider(riderId: Long): HandoffEventEntity?

    @Query("""
        SELECT e.* FROM handoff_events e
        INNER JOIN handoff_contacts c ON c.riderId = e.riderId AND c.phone = :phone
        WHERE e.outcome = 'PENDING'
        ORDER BY e.createdAt DESC LIMIT 1
    """)
    suspend fun getPendingEventForContactPhone(phone: String): HandoffEventEntity?
}
