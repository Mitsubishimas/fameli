package com.fameli.budget;

import com.fameli.budget.data.local.FameliDatabase;
import com.fameli.budget.data.repository.FamilyManager;
import com.fameli.budget.data.repository.FamilySyncRepository;
import com.fameli.budget.firebase.FirebaseAuthRepository;
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

  private final Provider<FamilyManager> familyManagerProvider;

  private final Provider<FirebaseAuthRepository> authRepositoryProvider;

  public FameliApp_MembersInjector(Provider<FameliDatabase> databaseProvider,
      Provider<FamilySyncRepository> familySyncRepositoryProvider,
      Provider<FamilyManager> familyManagerProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    this.databaseProvider = databaseProvider;
    this.familySyncRepositoryProvider = familySyncRepositoryProvider;
    this.familyManagerProvider = familyManagerProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  public static MembersInjector<FameliApp> create(Provider<FameliDatabase> databaseProvider,
      Provider<FamilySyncRepository> familySyncRepositoryProvider,
      Provider<FamilyManager> familyManagerProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    return new FameliApp_MembersInjector(databaseProvider, familySyncRepositoryProvider, familyManagerProvider, authRepositoryProvider);
  }

  @Override
  public void injectMembers(FameliApp instance) {
    injectDatabase(instance, databaseProvider.get());
    injectFamilySyncRepository(instance, familySyncRepositoryProvider.get());
    injectFamilyManager(instance, familyManagerProvider.get());
    injectAuthRepository(instance, authRepositoryProvider.get());
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

  @InjectedFieldSignature("com.fameli.budget.FameliApp.familyManager")
  public static void injectFamilyManager(FameliApp instance, FamilyManager familyManager) {
    instance.familyManager = familyManager;
  }

  @InjectedFieldSignature("com.fameli.budget.FameliApp.authRepository")
  public static void injectAuthRepository(FameliApp instance,
      FirebaseAuthRepository authRepository) {
    instance.authRepository = authRepository;
  }
}
