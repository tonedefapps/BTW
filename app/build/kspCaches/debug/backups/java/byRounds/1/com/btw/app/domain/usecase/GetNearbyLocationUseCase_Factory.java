package com.btw.app.domain.usecase;

import com.btw.app.domain.repository.LocationRepository;
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
public final class GetNearbyLocationUseCase_Factory implements Factory<GetNearbyLocationUseCase> {
  private final Provider<LocationRepository> repositoryProvider;

  public GetNearbyLocationUseCase_Factory(Provider<LocationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetNearbyLocationUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetNearbyLocationUseCase_Factory create(
      Provider<LocationRepository> repositoryProvider) {
    return new GetNearbyLocationUseCase_Factory(repositoryProvider);
  }

  public static GetNearbyLocationUseCase newInstance(LocationRepository repository) {
    return new GetNearbyLocationUseCase(repository);
  }
}
