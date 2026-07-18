package com.fameli.budget.ui.screens.transaction;

import com.fameli.budget.data.local.dao.CategoryDao;
import com.fameli.budget.data.local.dao.TransactionDao;
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

  public AddTransactionViewModel_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public AddTransactionViewModel get() {
    return newInstance(transactionDaoProvider.get(), categoryDaoProvider.get());
  }

  public static AddTransactionViewModel_Factory create(
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    return new AddTransactionViewModel_Factory(transactionDaoProvider, categoryDaoProvider);
  }

  public static AddTransactionViewModel newInstance(TransactionDao transactionDao,
      CategoryDao categoryDao) {
    return new AddTransactionViewModel(transactionDao, categoryDao);
  }
}
