package com.fameli.budget.data.repository;

import com.fameli.budget.data.local.dao.CategoryDao;
import com.fameli.budget.data.local.dao.ShoppingDao;
import com.fameli.budget.data.local.dao.TaskDao;
import com.fameli.budget.data.local.dao.TransactionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class FamilySyncRepository_Factory implements Factory<FamilySyncRepository> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<TaskDao> taskDaoProvider;

  private final Provider<ShoppingDao> shoppingDaoProvider;

  private final Provider<FamilyManager> familyManagerProvider;

  public FamilySyncRepository_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<TaskDao> taskDaoProvider,
      Provider<ShoppingDao> shoppingDaoProvider, Provider<FamilyManager> familyManagerProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.taskDaoProvider = taskDaoProvider;
    this.shoppingDaoProvider = shoppingDaoProvider;
    this.familyManagerProvider = familyManagerProvider;
  }

  @Override
  public FamilySyncRepository get() {
    return newInstance(transactionDaoProvider.get(), categoryDaoProvider.get(), taskDaoProvider.get(), shoppingDaoProvider.get(), familyManagerProvider.get());
  }

  public static FamilySyncRepository_Factory create(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<TaskDao> taskDaoProvider,
      Provider<ShoppingDao> shoppingDaoProvider, Provider<FamilyManager> familyManagerProvider) {
    return new FamilySyncRepository_Factory(transactionDaoProvider, categoryDaoProvider, taskDaoProvider, shoppingDaoProvider, familyManagerProvider);
  }

  public static FamilySyncRepository newInstance(TransactionDao transactionDao,
      CategoryDao categoryDao, TaskDao taskDao, ShoppingDao shoppingDao,
      FamilyManager familyManager) {
    return new FamilySyncRepository(transactionDao, categoryDao, taskDao, shoppingDao, familyManager);
  }
}
