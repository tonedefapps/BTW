package com.btw.app.di;

import com.btw.app.data.local.BtwDatabase;
import com.btw.app.data.local.dao.LocationDao;
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
public final class DatabaseModule_ProvideLocationDaoFactory implements Factory<LocationDao> {
  private final Provider<BtwDatabase> dbProvider;

  public DatabaseModule_ProvideLocationDaoFactory(Provider<BtwDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public LocationDao get() {
    return provideLocationDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideLocationDaoFactory create(Provider<BtwDatabase> dbProvider) {
    return new DatabaseModule_ProvideLocationDaoFactory(dbProvider);
  }

  public static LocationDao provideLocationDao(BtwDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideLocationDao(db));
  }
}
