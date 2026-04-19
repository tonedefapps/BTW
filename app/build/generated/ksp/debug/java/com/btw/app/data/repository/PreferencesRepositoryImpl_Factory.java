package com.btw.app.data.repository;

import com.btw.app.data.preferences.BtwPreferences;
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
public final class PreferencesRepositoryImpl_Factory implements Factory<PreferencesRepositoryImpl> {
  private final Provider<BtwPreferences> prefsProvider;

  public PreferencesRepositoryImpl_Factory(Provider<BtwPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public PreferencesRepositoryImpl get() {
    return newInstance(prefsProvider.get());
  }

  public static PreferencesRepositoryImpl_Factory create(Provider<BtwPreferences> prefsProvider) {
    return new PreferencesRepositoryImpl_Factory(prefsProvider);
  }

  public static PreferencesRepositoryImpl newInstance(BtwPreferences prefs) {
    return new PreferencesRepositoryImpl(prefs);
  }
}
