package com.tonedefapps.btw.di

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.WorkManager
import com.tonedefapps.btw.data.local.BtwDatabase
import com.tonedefapps.btw.data.local.dao.AlertDao
import com.tonedefapps.btw.data.local.dao.HandoffDao
import com.tonedefapps.btw.data.local.dao.LocationDao
import com.tonedefapps.btw.data.local.dao.RiderDao
import com.tonedefapps.btw.data.local.dao.VehicleDao
import com.tonedefapps.btw.data.preferences.BtwPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.SecureRandom
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BtwDatabase {
        val passphrase = getOrCreatePassphrase(context)
        return BtwDatabase.create(context, passphrase)
    }

    private fun getOrCreatePassphrase(context: Context): ByteArray {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val prefs = EncryptedSharedPreferences.create(
            context,
            "btw_db_secure",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val existing = prefs.getString("passphrase", null)
        if (existing != null) return Base64.decode(existing, Base64.DEFAULT)
        val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        prefs.edit().putString("passphrase", Base64.encodeToString(bytes, Base64.DEFAULT)).apply()
        return bytes
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
