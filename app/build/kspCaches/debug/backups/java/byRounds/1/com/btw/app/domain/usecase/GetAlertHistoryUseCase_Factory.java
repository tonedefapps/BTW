package com.btw.app.domain.usecase;

import com.btw.app.domain.repository.AlertRepository;
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
public final class GetAlertHistoryUseCase_Factory implements Factory<GetAlertHistoryUseCase> {
  private final Provider<AlertRepository> repositoryProvider;

  public GetAlertHistoryUseCase_Factory(Provider<AlertRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetAlertHistoryUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetAlertHistoryUseCase_Factory create(
      Provider<AlertRepository> repositoryProvider) {
    return new GetAlertHistoryUseCase_Factory(repositoryProvider);
  }

  public static GetAlertHistoryUseCase newInstance(AlertRepository repository) {
    return new GetAlertHistoryUseCase(repository);
  }
}
