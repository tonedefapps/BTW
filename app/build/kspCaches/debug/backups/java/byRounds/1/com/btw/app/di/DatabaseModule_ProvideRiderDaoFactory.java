package com.btw.app.di;

import com.btw.app.data.local.BtwDatabase;
import com.btw.app.data.local.dao.RiderDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideRiderDaoFactory implements Factory<RiderDao> {
  private final Provider<BtwDatabase> dbProvider;

  public DatabaseModule_ProvideRiderDaoFactory(Provider<BtwDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RiderDao get() {
    return provideRiderDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideRiderDaoFactory create(Provider<BtwDatabase> dbProvider) {
    return new DatabaseModule_ProvideRiderDaoFactory(dbProvider);
  }

  public static RiderDao provideRiderDao(BtwDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRiderDao(db));
  }
}
