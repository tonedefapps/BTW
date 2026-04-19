package com.btw.app.domain.usecase;

import com.btw.app.domain.repository.LocationRepository;
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
public final class RecordLocationVisitUseCase_Factory implements Factory<RecordLocationVisitUseCase> {
  private final Provider<LocationRepository> repositoryProvider;

  public RecordLocationVisitUseCase_Factory(Provider<LocationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RecordLocationVisitUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RecordLocationVisitUseCase_Factory create(
      Provider<LocationRepository> repositoryProvider) {
    return new RecordLocationVisitUseCase_Factory(repositoryProvider);
  }

  public static RecordLocationVisitUseCase newInstance(LocationRepository repository) {
    return new RecordLocationVisitUseCase(repository);
  }
}
