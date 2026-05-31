package com.tonedefapps.btw.di

import com.tonedefapps.btw.data.repository.*
import com.tonedefapps.btw.domain.repository.*
import com.tonedefapps.btw.data.repository.RiderScheduleRepositoryImpl
import com.tonedefapps.btw.domain.repository.RiderScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindRiderRepository(impl: RiderRepositoryImpl): RiderRepository

    @Binds @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository

    @Binds @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds @Singleton
    abstract fun bindHandoffRepository(impl: HandoffRepositoryImpl): HandoffRepository

    @Binds @Singleton
    abstract fun bindRiderScheduleRepository(impl: RiderScheduleRepositoryImpl): RiderScheduleRepository
}
