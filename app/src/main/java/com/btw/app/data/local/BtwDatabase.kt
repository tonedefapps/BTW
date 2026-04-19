package com.btw.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.btw.app.data.local.dao.*
import com.btw.app.data.local.entity.*
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
    ],
    version = 2,
    exportSchema = false
)
abstract class BtwDatabase : RoomDatabase() {
    abstract fun riderDao(): RiderDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun alertDao(): AlertDao
    abstract fun locationDao(): LocationDao
    abstract fun handoffDao(): HandoffDao

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
