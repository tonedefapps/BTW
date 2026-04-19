package com.btw.app.di

import android.content.Context
import androidx.work.WorkManager
import com.btw.app.data.local.BtwDatabase
import com.btw.app.data.local.dao.AlertDao
import com.btw.app.data.local.dao.HandoffDao
import com.btw.app.data.local.dao.LocationDao
import com.btw.app.data.local.dao.RiderDao
import com.btw.app.data.local.dao.VehicleDao
import com.btw.app.data.preferences.BtwPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BtwDatabase {
        // Derive a device-specific passphrase from Android Keystore in production;
        // using a fixed key here for initial scaffold — replace before release.
        val passphrase = "btw-secure-key-replace-me".toByteArray()
        return BtwDatabase.create(context, passphrase)
    }

    @Provides fun provideRiderDao(db: BtwDatabase): RiderDao = db.riderDao()
    @Provides fun provideVehicleDao(db: BtwDatabase): VehicleDao = db.vehicleDao()
    @Provides fun provideAlertDao(db: BtwDatabase): AlertDao = db.alertDao()
    @Provides fun provideLocationDao(db: BtwDatabase): LocationDao = db.locationDao()
    @Provides fun provideHandoffDao(db: BtwDatabase): HandoffDao = db.handoffDao()

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): BtwPreferences =
        BtwPreferences(context)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
}
