package com.fameli.budget.ui.screens.statistics;

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
public final class StatisticsViewModel_Factory implements Factory<StatisticsViewModel> {
  private final Provider<TransactionDao> daoProvider;

  public StatisticsViewModel_Factory(Provider<TransactionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public StatisticsViewModel get() {
    return newInstance(daoProvider.get());
  }

  public static StatisticsViewModel_Factory create(Provider<TransactionDao> daoProvider) {
    return new StatisticsViewModel_Factory(daoProvider);
  }

  public static StatisticsViewModel newInstance(TransactionDao dao) {
    return new StatisticsViewModel(dao);
  }
}
