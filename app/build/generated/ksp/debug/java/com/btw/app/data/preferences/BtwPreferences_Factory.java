package com.btw.app.data.preferences;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class BtwPreferences_Factory implements Factory<BtwPreferences> {
  private final Provider<Context> contextProvider;

  public BtwPreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BtwPreferences get() {
    return newInstance(contextProvider.get());
  }

  public static BtwPreferences_Factory create(Provider<Context> contextProvider) {
    return new BtwPreferences_Factory(contextProvider);
  }

  public static BtwPreferences newInstance(Context context) {
    return new BtwPreferences(context);
  }
}
