package com.btw.app.di;

import android.content.Context;
import com.btw.app.data.preferences.BtwPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvidePreferencesFactory implements Factory<BtwPreferences> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvidePreferencesFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BtwPreferences get() {
    return providePreferences(contextProvider.get());
  }

  public static DatabaseModule_ProvidePreferencesFactory create(Provider<Context> contextProvider) {
    return new DatabaseModule_ProvidePreferencesFactory(contextProvider);
  }

  public static BtwPreferences providePreferences(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePreferences(context));
  }
}
