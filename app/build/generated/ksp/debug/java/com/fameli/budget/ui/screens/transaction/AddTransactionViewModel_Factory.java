package com.fameli.budget.ui.screens.transaction;

import com.fameli.budget.data.local.dao.CategoryDao;
import com.fameli.budget.data.local.dao.TransactionDao;
import com.fameli.budget.data.repository.FamilySyncRepository;
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

  private final Provider<FamilySyncRepository> familyRepoProvider;

  public AddTransactionViewModel_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<FamilySyncRepository> familyRepoProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.familyRepoProvider = familyRepoProvider;
  }

  @Override
  public AddTransactionViewModel get() {
    return newInstance(transactionDaoProvider.get(), categoryDaoProvider.get(), familyRepoProvider.get());
  }

  public static AddTransactionViewModel_Factory create(
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<FamilySyncRepository> familyRepoProvider) {
    return new AddTransactionViewModel_Factory(transactionDaoProvider, categoryDaoProvider, familyRepoProvider);
  }

  public static AddTransactionViewModel newInstance(TransactionDao transactionDao,
      CategoryDao categoryDao, FamilySyncRepository familyRepo) {
    return new AddTransactionViewModel(transactionDao, categoryDao, familyRepo);
  }
}
