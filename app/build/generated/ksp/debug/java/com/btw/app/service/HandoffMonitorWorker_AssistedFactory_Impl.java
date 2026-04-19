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
public final class HandoffMonitorWorker_AssistedFactory_Impl implements HandoffMonitorWorker_AssistedFactory {
  private final HandoffMonitorWorker_Factory delegateFactory;

  HandoffMonitorWorker_AssistedFactory_Impl(HandoffMonitorWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public HandoffMonitorWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<HandoffMonitorWorker_AssistedFactory> create(
      HandoffMonitorWorker_Factory delegateFactory) {
    return InstanceFactory.create(new HandoffMonitorWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<HandoffMonitorWorker_AssistedFactory> createFactoryProvider(
      HandoffMonitorWorker_Factory delegateFactory) {
    return InstanceFactory.create(new HandoffMonitorWorker_AssistedFactory_Impl(delegateFactory));
  }
}
