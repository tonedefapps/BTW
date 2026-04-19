package com.btw.app.ui.home;

import android.app.Application;
import com.btw.app.domain.repository.AlertRepository;
import com.btw.app.domain.repository.PreferencesRepository;
import com.btw.app.domain.usecase.GetRidersUseCase;
import com.btw.app.domain.usecase.GetVehiclesUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<Application> applicationProvider;

  private final Provider<GetRidersUseCase> getRidersUseCaseProvider;

  private final Provider<GetVehiclesUseCase> getVehiclesUseCaseProvider;

  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public HomeViewModel_Factory(Provider<Application> applicationProvider,
      Provider<GetRidersUseCase> getRidersUseCaseProvider,
      Provider<GetVehiclesUseCase> getVehiclesUseCaseProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.applicationProvider = applicationProvider;
    this.getRidersUseCaseProvider = getRidersUseCaseProvider;
    this.getVehiclesUseCaseProvider = getVehiclesUseCaseProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(applicationProvider.get(), getRidersUseCaseProvider.get(), getVehiclesUseCaseProvider.get(), alertRepositoryProvider.get(), preferencesRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<Application> applicationProvider,
      Provider<GetRidersUseCase> getRidersUseCaseProvider,
      Provider<GetVehiclesUseCase> getVehiclesUseCaseProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new HomeViewModel_Factory(applicationProvider, getRidersUseCaseProvider, getVehiclesUseCaseProvider, alertRepositoryProvider, preferencesRepositoryProvider);
  }

  public static HomeViewModel newInstance(Application application,
      GetRidersUseCase getRidersUseCase, GetVehiclesUseCase getVehiclesUseCase,
      AlertRepository alertRepository, PreferencesRepository preferencesRepository) {
    return new HomeViewModel(application, getRidersUseCase, getVehiclesUseCase, alertRepository, preferencesRepository);
  }
}
