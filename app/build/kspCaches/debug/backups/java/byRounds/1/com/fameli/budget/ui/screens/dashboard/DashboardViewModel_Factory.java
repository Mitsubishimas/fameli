package com.fameli.budget.ui.screens.dashboard;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<TransactionDao> daoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public DashboardViewModel_Factory(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.daoProvider = daoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(daoProvider.get(), categoryDaoProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    return new DashboardViewModel_Factory(daoProvider, categoryDaoProvider);
  }

  public static DashboardViewModel newInstance(TransactionDao dao, CategoryDao categoryDao) {
    return new DashboardViewModel(dao, categoryDao);
  }
}
