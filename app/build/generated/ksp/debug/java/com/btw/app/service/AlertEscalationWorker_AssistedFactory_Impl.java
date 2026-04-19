package com.btw.app.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AlertEscalationWorker_AssistedFactory_Impl implements AlertEscalationWorker_AssistedFactory {
  private final AlertEscalationWorker_Factory delegateFactory;

  AlertEscalationWorker_AssistedFactory_Impl(AlertEscalationWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public AlertEscalationWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<AlertEscalationWorker_AssistedFactory> create(
      AlertEscalationWorker_Factory delegateFactory) {
    return InstanceFactory.create(new AlertEscalationWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<AlertEscalationWorker_AssistedFactory> createFactoryProvider(
      AlertEscalationWorker_Factory delegateFactory) {
    return InstanceFactory.create(new AlertEscalationWorker_AssistedFactory_Impl(delegateFactory));
  }
}
