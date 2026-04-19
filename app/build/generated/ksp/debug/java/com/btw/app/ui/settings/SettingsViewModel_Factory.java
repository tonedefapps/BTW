package com.btw.app.ui.settings;

import com.btw.app.domain.repository.PreferencesRepository;
import com.btw.app.domain.repository.RiderRepository;
import com.btw.app.domain.usecase.AddRiderUseCase;
import com.btw.app.domain.usecase.GetAlertHistoryUseCase;
import com.btw.app.domain.usecase.GetRidersUseCase;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<GetRidersUseCase> getRidersUseCaseProvider;

  private final Provider<GetAlertHistoryUseCase> getAlertHistoryUseCaseProvider;

  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  private final Provider<RiderRepository> riderRepositoryProvider;

  private final Provider<AddRiderUseCase> addRiderUseCaseProvider;

  public SettingsViewModel_Factory(Provider<GetRidersUseCase> getRidersUseCaseProvider,
      Provider<GetAlertHistoryUseCase> getAlertHistoryUseCaseProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<RiderRepository> riderRepositoryProvider,
      Provider<AddRiderUseCase> addRiderUseCaseProvider) {
    this.getRidersUseCaseProvider = getRidersUseCaseProvider;
    this.getAlertHistoryUseCaseProvider = getAlertHistoryUseCaseProvider;
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.riderRepositoryProvider = riderRepositoryProvider;
    this.addRiderUseCaseProvider = addRiderUseCaseProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(getRidersUseCaseProvider.get(), getAlertHistoryUseCaseProvider.get(), preferencesRepositoryProvider.get(), riderRepositoryProvider.get(), addRiderUseCaseProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<GetRidersUseCase> getRidersUseCaseProvider,
      Provider<GetAlertHistoryUseCase> getAlertHistoryUseCaseProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<RiderRepository> riderRepositoryProvider,
      Provider<AddRiderUseCase> addRiderUseCaseProvider) {
    return new SettingsViewModel_Factory(getRidersUseCaseProvider, getAlertHistoryUseCaseProvider, preferencesRepositoryProvider, riderRepositoryProvider, addRiderUseCaseProvider);
  }

  public static SettingsViewModel newInstance(GetRidersUseCase getRidersUseCase,
      GetAlertHistoryUseCase getAlertHistoryUseCase, PreferencesRepository preferencesRepository,
      RiderRepository riderRepository, AddRiderUseCase addRiderUseCase) {
    return new SettingsViewModel(getRidersUseCase, getAlertHistoryUseCase, preferencesRepository, riderRepository, addRiderUseCase);
  }
}
