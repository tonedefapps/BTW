package com.btw.app.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.btw.app.data.local.dao.AlertDao
import com.btw.app.data.local.dao.RiderDao
import com.btw.app.data.local.dao.VehicleDao
import com.btw.app.data.local.entity.AlertEntity
import com.btw.app.data.local.entity.RiderEntity
import com.btw.app.data.local.entity.VehicleEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [RiderEntity::class, VehicleEntity::class, AlertEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BtwDatabase : RoomDatabase() {
    abstract fun riderDao(): RiderDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun alertDao(): AlertDao

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
