package com.fameli.budget.ui.screens.budget;

import com.fameli.budget.data.local.dao.BudgetDao;
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
public final class BudgetViewModel_Factory implements Factory<BudgetViewModel> {
  private final Provider<BudgetDao> budgetDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<FamilySyncRepository> syncRepositoryProvider;

  public BudgetViewModel_Factory(Provider<BudgetDao> budgetDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<TransactionDao> transactionDaoProvider,
      Provider<FamilySyncRepository> syncRepositoryProvider) {
    this.budgetDaoProvider = budgetDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  @Override
  public BudgetViewModel get() {
    return newInstance(budgetDaoProvider.get(), categoryDaoProvider.get(), transactionDaoProvider.get(), syncRepositoryProvider.get());
  }

  public static BudgetViewModel_Factory create(Provider<BudgetDao> budgetDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<TransactionDao> transactionDaoProvider,
      Provider<FamilySyncRepository> syncRepositoryProvider) {
    return new BudgetViewModel_Factory(budgetDaoProvider, categoryDaoProvider, transactionDaoProvider, syncRepositoryProvider);
  }

  public static BudgetViewModel newInstance(BudgetDao budgetDao, CategoryDao categoryDao,
      TransactionDao transactionDao, FamilySyncRepository syncRepository) {
    return new BudgetViewModel(budgetDao, categoryDao, transactionDao, syncRepository);
  }
}
