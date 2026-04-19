package com.btw.app.ui.locations;

import com.btw.app.domain.repository.LocationRepository;
import com.btw.app.domain.usecase.GetLocationsUseCase;
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
public final class LocationsViewModel_Factory implements Factory<LocationsViewModel> {
  private final Provider<GetLocationsUseCase> getLocationsUseCaseProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  public LocationsViewModel_Factory(Provider<GetLocationsUseCase> getLocationsUseCaseProvider,
      Provider<LocationRepository> locationRepositoryProvider) {
    this.getLocationsUseCaseProvider = getLocationsUseCaseProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
  }

  @Override
  public LocationsViewModel get() {
    return newInstance(getLocationsUseCaseProvider.get(), locationRepositoryProvider.get());
  }

  public static LocationsViewModel_Factory create(
      Provider<GetLocationsUseCase> getLocationsUseCaseProvider,
      Provider<LocationRepository> locationRepositoryProvider) {
    return new LocationsViewModel_Factory(getLocationsUseCaseProvider, locationRepositoryProvider);
  }

  public static LocationsViewModel newInstance(GetLocationsUseCase getLocationsUseCase,
      LocationRepository locationRepository) {
    return new LocationsViewModel(getLocationsUseCase, locationRepository);
  }
}
