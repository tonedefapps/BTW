package com.btw.app.di;

import com.btw.app.data.local.BtwDatabase;
import com.btw.app.data.local.dao.HandoffDao;
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
public final class DatabaseModule_ProvideHandoffDaoFactory implements Factory<HandoffDao> {
  private final Provider<BtwDatabase> dbProvider;

  public DatabaseModule_ProvideHandoffDaoFactory(Provider<BtwDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public HandoffDao get() {
    return provideHandoffDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideHandoffDaoFactory create(Provider<BtwDatabase> dbProvider) {
    return new DatabaseModule_ProvideHandoffDaoFactory(dbProvider);
  }

  public static HandoffDao provideHandoffDao(BtwDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideHandoffDao(db));
  }
}
