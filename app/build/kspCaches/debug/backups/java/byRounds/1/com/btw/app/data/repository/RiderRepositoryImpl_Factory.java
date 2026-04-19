package com.btw.app.data.repository;

import com.btw.app.data.local.dao.RiderDao;
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
public final class RiderRepositoryImpl_Factory implements Factory<RiderRepositoryImpl> {
  private final Provider<RiderDao> daoProvider;

  public RiderRepositoryImpl_Factory(Provider<RiderDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RiderRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static RiderRepositoryImpl_Factory create(Provider<RiderDao> daoProvider) {
    return new RiderRepositoryImpl_Factory(daoProvider);
  }

  public static RiderRepositoryImpl newInstance(RiderDao dao) {
    return new RiderRepositoryImpl(dao);
  }
}
