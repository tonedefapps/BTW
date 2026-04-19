package com.btw.app.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.btw.app.domain.repository.HandoffRepository;
import com.btw.app.domain.repository.LocationRepository;
import com.btw.app.domain.repository.RiderRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class HandoffMonitorWorker_Factory {
  private final Provider<HandoffRepository> handoffRepositoryProvider;

  private final Provider<RiderRepository> riderRepositoryProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  public HandoffMonitorWorker_Factory(Provider<HandoffRepository> handoffRepositoryProvider,
      Provider<RiderRepository> riderRepositoryProvider,
      Provider<LocationRepository> locationRepositoryProvider) {
    this.handoffRepositoryProvider = handoffRepositoryProvider;
    this.riderRepositoryProvider = riderRepositoryProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
  }

  public HandoffMonitorWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, handoffRepositoryProvider.get(), riderRepositoryProvider.get(), locationRepositoryProvider.get());
  }

  public static HandoffMonitorWorker_Factory create(
      Provider<HandoffRepository> handoffRepositoryProvider,
      Provider<RiderRepository> riderRepositoryProvider,
      Provider<LocationRepository> locationRepositoryProvider) {
    return new HandoffMonitorWorker_Factory(handoffRepositoryProvider, riderRepositoryProvider, locationRepositoryProvider);
  }

  public static HandoffMonitorWorker newInstance(Context context, WorkerParameters params,
      HandoffRepository handoffRepository, RiderRepository riderRepository,
      LocationRepository locationRepository) {
    return new HandoffMonitorWorker(context, params, handoffRepository, riderRepository, locationRepository);
  }
}
