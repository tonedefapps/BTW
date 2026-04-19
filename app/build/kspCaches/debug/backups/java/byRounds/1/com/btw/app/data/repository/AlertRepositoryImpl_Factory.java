package com.btw.app.data.repository;

import com.btw.app.data.local.dao.AlertDao;
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
public final class AlertRepositoryImpl_Factory implements Factory<AlertRepositoryImpl> {
  private final Provider<AlertDao> daoProvider;

  public AlertRepositoryImpl_Factory(Provider<AlertDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public AlertRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static AlertRepositoryImpl_Factory create(Provider<AlertDao> daoProvider) {
    return new AlertRepositoryImpl_Factory(daoProvider);
  }

  public static AlertRepositoryImpl newInstance(AlertDao dao) {
    return new AlertRepositoryImpl(dao);
  }
}
