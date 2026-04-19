package com.btw.app.ui.setup;

import com.btw.app.domain.usecase.AddRiderUseCase;
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
public final class AddRiderViewModel_Factory implements Factory<AddRiderViewModel> {
  private final Provider<AddRiderUseCase> addRiderUseCaseProvider;

  public AddRiderViewModel_Factory(Provider<AddRiderUseCase> addRiderUseCaseProvider) {
    this.addRiderUseCaseProvider = addRiderUseCaseProvider;
  }

  @Override
  public AddRiderViewModel get() {
    return newInstance(addRiderUseCaseProvider.get());
  }

  public static AddRiderViewModel_Factory create(
      Provider<AddRiderUseCase> addRiderUseCaseProvider) {
    return new AddRiderViewModel_Factory(addRiderUseCaseProvider);
  }

  public static AddRiderViewModel newInstance(AddRiderUseCase addRiderUseCase) {
    return new AddRiderViewModel(addRiderUseCase);
  }
}
