package com.btw.app.data.repository;

import com.btw.app.data.local.dao.LocationDao;
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
public final class LocationRepositoryImpl_Factory implements Factory<LocationRepositoryImpl> {
  private final Provider<LocationDao> daoProvider;

  public LocationRepositoryImpl_Factory(Provider<LocationDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public LocationRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static LocationRepositoryImpl_Factory create(Provider<LocationDao> daoProvider) {
    return new LocationRepositoryImpl_Factory(daoProvider);
  }

  public static LocationRepositoryImpl newInstance(LocationDao dao) {
    return new LocationRepositoryImpl(dao);
  }
}
