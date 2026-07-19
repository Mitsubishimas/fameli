package com.fameli.budget;

import com.fameli.budget.data.local.FameliDatabase;
import com.fameli.budget.data.repository.FamilySyncRepository;
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
    "KotlinInternalInJava",
    "cast"
})
public final class FameliApp_MembersInjector implements MembersInjector<FameliApp> {
  private final Provider<FameliDatabase> databaseProvider;

  private final Provider<FamilySyncRepository> familySyncRepositoryProvider;

  public FameliApp_MembersInjector(Provider<FameliDatabase> databaseProvider,
      Provider<FamilySyncRepository> familySyncRepositoryProvider) {
    this.databaseProvider = databaseProvider;
    this.familySyncRepositoryProvider = familySyncRepositoryProvider;
  }

  public static MembersInjector<FameliApp> create(Provider<FameliDatabase> databaseProvider,
      Provider<FamilySyncRepository> familySyncRepositoryProvider) {
    return new FameliApp_MembersInjector(databaseProvider, familySyncRepositoryProvider);
  }

  @Override
  public void injectMembers(FameliApp instance) {
    injectDatabase(instance, databaseProvider.get());
    injectFamilySyncRepository(instance, familySyncRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.fameli.budget.FameliApp.database")
  public static void injectDatabase(FameliApp instance, FameliDatabase database) {
    instance.database = database;
  }

  @InjectedFieldSignature("com.fameli.budget.FameliApp.familySyncRepository")
  public static void injectFamilySyncRepository(FameliApp instance,
      FamilySyncRepository familySyncRepository) {
    instance.familySyncRepository = familySyncRepository;
  }
}
