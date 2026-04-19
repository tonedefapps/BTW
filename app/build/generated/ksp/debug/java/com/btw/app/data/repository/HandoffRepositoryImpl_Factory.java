package com.btw.app.data.repository;

import com.btw.app.data.local.dao.HandoffDao;
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
public final class HandoffRepositoryImpl_Factory implements Factory<HandoffRepositoryImpl> {
  private final Provider<HandoffDao> daoProvider;

  public HandoffRepositoryImpl_Factory(Provider<HandoffDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public HandoffRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static HandoffRepositoryImpl_Factory create(Provider<HandoffDao> daoProvider) {
    return new HandoffRepositoryImpl_Factory(daoProvider);
  }

  public static HandoffRepositoryImpl newInstance(HandoffDao dao) {
    return new HandoffRepositoryImpl(dao);
  }
}
