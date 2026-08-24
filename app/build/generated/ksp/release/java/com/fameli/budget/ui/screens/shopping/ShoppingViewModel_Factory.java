package com.fameli.budget.ui.screens.shopping;

import com.fameli.budget.data.local.dao.ShoppingDao;
import com.fameli.budget.data.repository.FamilyManager;
import com.fameli.budget.data.repository.FamilySyncRepository;
import com.fameli.budget.firebase.FirebaseAuthRepository;
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
    "KotlinInternalInJava",
    "cast"
})
public final class ShoppingViewModel_Factory implements Factory<ShoppingViewModel> {
  private final Provider<ShoppingDao> shoppingDaoProvider;

  private final Provider<FirebaseAuthRepository> authRepositoryProvider;

  private final Provider<FamilySyncRepository> familyRepoProvider;

  private final Provider<FamilyManager> familyManagerProvider;

  public ShoppingViewModel_Factory(Provider<ShoppingDao> shoppingDaoProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider,
      Provider<FamilySyncRepository> familyRepoProvider,
      Provider<FamilyManager> familyManagerProvider) {
    this.shoppingDaoProvider = shoppingDaoProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.familyRepoProvider = familyRepoProvider;
    this.familyManagerProvider = familyManagerProvider;
  }

  @Override
  public ShoppingViewModel get() {
    return newInstance(shoppingDaoProvider.get(), authRepositoryProvider.get(), familyRepoProvider.get(), familyManagerProvider.get());
  }

  public static ShoppingViewModel_Factory create(Provider<ShoppingDao> shoppingDaoProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider,
      Provider<FamilySyncRepository> familyRepoProvider,
      Provider<FamilyManager> familyManagerProvider) {
    return new ShoppingViewModel_Factory(shoppingDaoProvider, authRepositoryProvider, familyRepoProvider, familyManagerProvider);
  }

  public static ShoppingViewModel newInstance(ShoppingDao shoppingDao,
      FirebaseAuthRepository authRepository, FamilySyncRepository familyRepo,
      FamilyManager familyManager) {
    return new ShoppingViewModel(shoppingDao, authRepository, familyRepo, familyManager);
  }
}
