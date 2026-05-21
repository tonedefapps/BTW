package com.tonedefapps.btw.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tonedefapps.btw.data.local.dao.*
import com.tonedefapps.btw.data.local.entity.*
import com.tonedefapps.btw.data.local.dao.RiderScheduleDao
import com.tonedefapps.btw.data.local.entity.RiderScheduleEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        RiderEntity::class,
        VehicleEntity::class,
        AlertEntity::class,
        SavedLocationEntity::class,
        RiderLocationStatsEntity::class,
        HandoffContactEntity::class,
        PickupWindowEntity::class,
        HandoffEventEntity::class,
        RiderScheduleEntity::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class BtwDatabase : RoomDatabase() {
    abstract fun riderDao(): RiderDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun alertDao(): AlertDao
    abstract fun locationDao(): LocationDao
    abstract fun handoffDao(): HandoffDao
    abstract fun riderScheduleDao(): RiderScheduleDao

    companion object {
        private const val DB_NAME = "btw.db"

        fun create(context: Context, passphrase: ByteArray): BtwDatabase {
            val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase.map { it.toChar() }.toCharArray()))
            return Room.databaseBuilder(context, BtwDatabase::class.java, DB_NAME)
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
