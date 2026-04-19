package com.btw.app.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.btw.app.data.local.BtwDatabase;
import com.btw.app.data.preferences.BtwPreferences;
import dagger.internal.DaggerGenerated;
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
public final class AlertEscalationWorker_Factory {
  private final Provider<BtwDatabase> databaseProvider;

  private final Provider<BtwPreferences> preferencesProvider;

  public AlertEscalationWorker_Factory(Provider<BtwDatabase> databaseProvider,
      Provider<BtwPreferences> preferencesProvider) {
    this.databaseProvider = databaseProvider;
    this.preferencesProvider = preferencesProvider;
  }

  public AlertEscalationWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, databaseProvider.get(), preferencesProvider.get());
  }

  public static AlertEscalationWorker_Factory create(Provider<BtwDatabase> databaseProvider,
      Provider<BtwPreferences> preferencesProvider) {
    return new AlertEscalationWorker_Factory(databaseProvider, preferencesProvider);
  }

  public static AlertEscalationWorker newInstance(Context context, WorkerParameters params,
      BtwDatabase database, BtwPreferences preferences) {
    return new AlertEscalationWorker(context, params, database, preferences);
  }
}
