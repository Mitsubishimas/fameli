package com.fameli.budget.ui.screens.shopping;

import com.fameli.budget.data.local.dao.ShoppingDao;
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

  public ShoppingViewModel_Factory(Provider<ShoppingDao> shoppingDaoProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    this.shoppingDaoProvider = shoppingDaoProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public ShoppingViewModel get() {
    return newInstance(shoppingDaoProvider.get(), authRepositoryProvider.get());
  }

  public static ShoppingViewModel_Factory create(Provider<ShoppingDao> shoppingDaoProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    return new ShoppingViewModel_Factory(shoppingDaoProvider, authRepositoryProvider);
  }

  public static ShoppingViewModel newInstance(ShoppingDao shoppingDao,
      FirebaseAuthRepository authRepository) {
    return new ShoppingViewModel(shoppingDao, authRepository);
  }
}
