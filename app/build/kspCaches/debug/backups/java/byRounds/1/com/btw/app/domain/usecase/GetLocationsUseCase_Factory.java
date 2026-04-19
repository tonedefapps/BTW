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
public final class GetLocationsUseCase_Factory implements Factory<GetLocationsUseCase> {
  private final Provider<LocationRepository> repositoryProvider;

  public GetLocationsUseCase_Factory(Provider<LocationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetLocationsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetLocationsUseCase_Factory create(
      Provider<LocationRepository> repositoryProvider) {
    return new GetLocationsUseCase_Factory(repositoryProvider);
  }

  public static GetLocationsUseCase newInstance(LocationRepository repository) {
    return new GetLocationsUseCase(repository);
  }
}
