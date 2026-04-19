package com.btw.app.domain.usecase;

import com.btw.app.domain.repository.HandoffRepository;
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
public final class CheckExpectedPickupUseCase_Factory implements Factory<CheckExpectedPickupUseCase> {
  private final Provider<HandoffRepository> repositoryProvider;

  public CheckExpectedPickupUseCase_Factory(Provider<HandoffRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CheckExpectedPickupUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CheckExpectedPickupUseCase_Factory create(
      Provider<HandoffRepository> repositoryProvider) {
    return new CheckExpectedPickupUseCase_Factory(repositoryProvider);
  }

  public static CheckExpectedPickupUseCase newInstance(HandoffRepository repository) {
    return new CheckExpectedPickupUseCase(repository);
  }
}
