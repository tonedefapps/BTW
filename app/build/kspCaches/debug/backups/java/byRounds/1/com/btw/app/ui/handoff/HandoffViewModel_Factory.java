package com.btw.app.ui.handoff;

import com.btw.app.domain.repository.HandoffRepository;
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
public final class HandoffViewModel_Factory implements Factory<HandoffViewModel> {
  private final Provider<HandoffRepository> handoffRepositoryProvider;

  private final Provider<GetLocationsUseCase> getLocationsUseCaseProvider;

  public HandoffViewModel_Factory(Provider<HandoffRepository> handoffRepositoryProvider,
      Provider<GetLocationsUseCase> getLocationsUseCaseProvider) {
    this.handoffRepositoryProvider = handoffRepositoryProvider;
    this.getLocationsUseCaseProvider = getLocationsUseCaseProvider;
  }

  @Override
  public HandoffViewModel get() {
    return newInstance(handoffRepositoryProvider.get(), getLocationsUseCaseProvider.get());
  }

  public static HandoffViewModel_Factory create(
      Provider<HandoffRepository> handoffRepositoryProvider,
      Provider<GetLocationsUseCase> getLocationsUseCaseProvider) {
    return new HandoffViewModel_Factory(handoffRepositoryProvider, getLocationsUseCaseProvider);
  }

  public static HandoffViewModel newInstance(HandoffRepository handoffRepository,
      GetLocationsUseCase getLocationsUseCase) {
    return new HandoffViewModel(handoffRepository, getLocationsUseCase);
  }
}
