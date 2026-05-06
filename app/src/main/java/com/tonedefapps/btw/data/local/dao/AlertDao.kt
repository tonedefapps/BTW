package com.tonedefapps.btw.data.local.dao

import androidx.room.*
import com.tonedefapps.btw.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alert_events ORDER BY triggeredAt DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity): Long

    @Query("UPDATE alert_events SET outcome = :outcome, acknowledgedAt = :acknowledgedAt WHERE id = :id")
    suspend fun updateOutcome(id: Long, outcome: String, acknowledgedAt: Long)

    @Query("DELETE FROM alert_events WHERE id = :id")
    suspend fun deleteById(id: Long)
}
