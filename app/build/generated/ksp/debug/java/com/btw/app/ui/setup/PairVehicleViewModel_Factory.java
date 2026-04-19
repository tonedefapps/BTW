package com.btw.app.ui.setup;

import com.btw.app.domain.repository.VehicleRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PairVehicleViewModel_Factory implements Factory<PairVehicleViewModel> {
  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  public PairVehicleViewModel_Factory(Provider<VehicleRepository> vehicleRepositoryProvider) {
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
  }

  @Override
  public PairVehicleViewModel get() {
    return newInstance(vehicleRepositoryProvider.get());
  }

  public static PairVehicleViewModel_Factory create(
      Provider<VehicleRepository> vehicleRepositoryProvider) {
    return new PairVehicleViewModel_Factory(vehicleRepositoryProvider);
  }

  public static PairVehicleViewModel newInstance(VehicleRepository vehicleRepository) {
    return new PairVehicleViewModel(vehicleRepository);
  }
}
