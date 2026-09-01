package com.fameli.budget.ui.screens.dashboard;

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

  public DashboardViewModel_Factory(Provider<TransactionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(daoProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<TransactionDao> daoProvider) {
    return new DashboardViewModel_Factory(daoProvider);
  }

  public static DashboardViewModel newInstance(TransactionDao dao) {
    return new DashboardViewModel(dao);
  }
}
