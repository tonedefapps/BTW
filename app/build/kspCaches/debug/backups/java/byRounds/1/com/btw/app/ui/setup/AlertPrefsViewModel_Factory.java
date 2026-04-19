package com.btw.app.ui.setup;

import com.btw.app.domain.repository.PreferencesRepository;
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
public final class AlertPrefsViewModel_Factory implements Factory<AlertPrefsViewModel> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public AlertPrefsViewModel_Factory(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  @Override
  public AlertPrefsViewModel get() {
    return newInstance(preferencesRepositoryProvider.get());
  }

  public static AlertPrefsViewModel_Factory create(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new AlertPrefsViewModel_Factory(preferencesRepositoryProvider);
  }

  public static AlertPrefsViewModel newInstance(PreferencesRepository preferencesRepository) {
    return new AlertPrefsViewModel(preferencesRepository);
  }
}
