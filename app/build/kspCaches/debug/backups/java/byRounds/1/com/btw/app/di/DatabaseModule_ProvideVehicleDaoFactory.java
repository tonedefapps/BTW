package com.btw.app.di;

import com.btw.app.data.local.BtwDatabase;
import com.btw.app.data.local.dao.VehicleDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideVehicleDaoFactory implements Factory<VehicleDao> {
  private final Provider<BtwDatabase> dbProvider;

  public DatabaseModule_ProvideVehicleDaoFactory(Provider<BtwDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public VehicleDao get() {
    return provideVehicleDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideVehicleDaoFactory create(Provider<BtwDatabase> dbProvider) {
    return new DatabaseModule_ProvideVehicleDaoFactory(dbProvider);
  }

  public static VehicleDao provideVehicleDao(BtwDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideVehicleDao(db));
  }
}
