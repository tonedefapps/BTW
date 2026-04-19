package com.btw.app.service;

import androidx.work.WorkManager;
import com.btw.app.data.local.BtwDatabase;
import com.btw.app.domain.usecase.CheckExpectedPickupUseCase;
import com.btw.app.domain.usecase.GetNearbyLocationUseCase;
import com.btw.app.domain.usecase.RecordLocationVisitUseCase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BtwMonitorService_MembersInjector implements MembersInjector<BtwMonitorService> {
  private final Provider<BtwDatabase> databaseProvider;

  private final Provider<WorkManager> workManagerProvider;

  private final Provider<GetNearbyLocationUseCase> getNearbyLocationProvider;

  private final Provider<RecordLocationVisitUseCase> recordLocationVisitProvider;

  private final Provider<CheckExpectedPickupUseCase> checkExpectedPickupProvider;

  public BtwMonitorService_MembersInjector(Provider<BtwDatabase> databaseProvider,
      Provider<WorkManager> workManagerProvider,
      Provider<GetNearbyLocationUseCase> getNearbyLocationProvider,
      Provider<RecordLocationVisitUseCase> recordLocationVisitProvider,
      Provider<CheckExpectedPickupUseCase> checkExpectedPickupProvider) {
    this.databaseProvider = databaseProvider;
    this.workManagerProvider = workManagerProvider;
    this.getNearbyLocationProvider = getNearbyLocationProvider;
    this.recordLocationVisitProvider = recordLocationVisitProvider;
    this.checkExpectedPickupProvider = checkExpectedPickupProvider;
  }

  public static MembersInjector<BtwMonitorService> create(Provider<BtwDatabase> databaseProvider,
      Provider<WorkManager> workManagerProvider,
      Provider<GetNearbyLocationUseCase> getNearbyLocationProvider,
      Provider<RecordLocationVisitUseCase> recordLocationVisitProvider,
      Provider<CheckExpectedPickupUseCase> checkExpectedPickupProvider) {
    return new BtwMonitorService_MembersInjector(databaseProvider, workManagerProvider, getNearbyLocationProvider, recordLocationVisitProvider, checkExpectedPickupProvider);
  }

  @Override
  public void injectMembers(BtwMonitorService instance) {
    injectDatabase(instance, databaseProvider.get());
    injectWorkManager(instance, workManagerProvider.get());
    injectGetNearbyLocation(instance, getNearbyLocationProvider.get());
    injectRecordLocationVisit(instance, recordLocationVisitProvider.get());
    injectCheckExpectedPickup(instance, checkExpectedPickupProvider.get());
  }

  @InjectedFieldSignature("com.btw.app.service.BtwMonitorService.database")
  public static void injectDatabase(BtwMonitorService instance, BtwDatabase database) {
    instance.database = database;
  }

  @InjectedFieldSignature("com.btw.app.service.BtwMonitorService.workManager")
  public static void injectWorkManager(BtwMonitorService instance, WorkManager workManager) {
    instance.workManager = workManager;
  }

  @InjectedFieldSignature("com.btw.app.service.BtwMonitorService.getNearbyLocation")
  public static void injectGetNearbyLocation(BtwMonitorService instance,
      GetNearbyLocationUseCase getNearbyLocation) {
    instance.getNearbyLocation = getNearbyLocation;
  }

  @InjectedFieldSignature("com.btw.app.service.BtwMonitorService.recordLocationVisit")
  public static void injectRecordLocationVisit(BtwMonitorService instance,
      RecordLocationVisitUseCase recordLocationVisit) {
    instance.recordLocationVisit = recordLocationVisit;
  }

  @InjectedFieldSignature("com.btw.app.service.BtwMonitorService.checkExpectedPickup")
  public static void injectCheckExpectedPickup(BtwMonitorService instance,
      CheckExpectedPickupUseCase checkExpectedPickup) {
    instance.checkExpectedPickup = checkExpectedPickup;
  }
}
