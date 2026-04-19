package com.btw.app.service;

import com.btw.app.data.local.BtwDatabase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SmsVerificationReceiver_MembersInjector implements MembersInjector<SmsVerificationReceiver> {
  private final Provider<BtwDatabase> databaseProvider;

  public SmsVerificationReceiver_MembersInjector(Provider<BtwDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  public static MembersInjector<SmsVerificationReceiver> create(
      Provider<BtwDatabase> databaseProvider) {
    return new SmsVerificationReceiver_MembersInjector(databaseProvider);
  }

  @Override
  public void injectMembers(SmsVerificationReceiver instance) {
    injectDatabase(instance, databaseProvider.get());
  }

  @InjectedFieldSignature("com.btw.app.service.SmsVerificationReceiver.database")
  public static void injectDatabase(SmsVerificationReceiver instance, BtwDatabase database) {
    instance.database = database;
  }
}
