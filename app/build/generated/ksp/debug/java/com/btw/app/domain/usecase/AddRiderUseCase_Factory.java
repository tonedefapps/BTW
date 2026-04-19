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
public final class AddRiderUseCase_Factory implements Factory<AddRiderUseCase> {
  private final Provider<RiderRepository> repositoryProvider;

  public AddRiderUseCase_Factory(Provider<RiderRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AddRiderUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static AddRiderUseCase_Factory create(Provider<RiderRepository> repositoryProvider) {
    return new AddRiderUseCase_Factory(repositoryProvider);
  }

  public static AddRiderUseCase newInstance(RiderRepository repository) {
    return new AddRiderUseCase(repository);
  }
}
