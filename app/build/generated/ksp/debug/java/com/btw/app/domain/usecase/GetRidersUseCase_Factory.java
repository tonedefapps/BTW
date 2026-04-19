package com.btw.app.domain.usecase;

import com.btw.app.domain.repository.RiderRepository;
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
public final class GetRidersUseCase_Factory implements Factory<GetRidersUseCase> {
  private final Provider<RiderRepository> repositoryProvider;

  public GetRidersUseCase_Factory(Provider<RiderRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetRidersUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetRidersUseCase_Factory create(Provider<RiderRepository> repositoryProvider) {
    return new GetRidersUseCase_Factory(repositoryProvider);
  }

  public static GetRidersUseCase newInstance(RiderRepository repository) {
    return new GetRidersUseCase(repository);
  }
}
