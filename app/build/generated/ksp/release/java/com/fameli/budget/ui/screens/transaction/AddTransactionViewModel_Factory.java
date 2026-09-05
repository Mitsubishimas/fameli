package com.fameli.budget.ui.screens.transaction;

import com.fameli.budget.data.local.dao.CategoryDao;
import com.fameli.budget.data.local.dao.TransactionDao;
import com.fameli.budget.data.repository.FamilyManager;
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
public final class AddTransactionViewModel_Factory implements Factory<AddTransactionViewModel> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<FamilyManager> familyManagerProvider;

  public AddTransactionViewModel_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<FamilyManager> familyManagerProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.familyManagerProvider = familyManagerProvider;
  }

  @Override
  public AddTransactionViewModel get() {
    return newInstance(transactionDaoProvider.get(), categoryDaoProvider.get(), familyManagerProvider.get());
  }

  public static AddTransactionViewModel_Factory create(
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<FamilyManager> familyManagerProvider) {
    return new AddTransactionViewModel_Factory(transactionDaoProvider, categoryDaoProvider, familyManagerProvider);
  }

  public static AddTransactionViewModel newInstance(TransactionDao transactionDao,
      CategoryDao categoryDao, FamilyManager familyManager) {
    return new AddTransactionViewModel(transactionDao, categoryDao, familyManager);
  }
}
