package com.fameli.budget.data.repository;

import com.fameli.budget.data.local.dao.CategoryDao;
import com.fameli.budget.data.local.dao.GoalDao;
import com.fameli.budget.data.local.dao.ShoppingDao;
import com.fameli.budget.data.local.dao.TaskDao;
import com.fameli.budget.data.local.dao.TransactionDao;
import com.fameli.budget.firebase.FirebaseAuthRepository;
import com.google.firebase.firestore.FirebaseFirestore;
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
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuthRepository> authRepositoryProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<TaskDao> taskDaoProvider;

  private final Provider<GoalDao> goalDaoProvider;

  private final Provider<ShoppingDao> shoppingDaoProvider;

  private final Provider<FamilyManager> familyManagerProvider;

  public FamilySyncRepository_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<TaskDao> taskDaoProvider, Provider<GoalDao> goalDaoProvider,
      Provider<ShoppingDao> shoppingDaoProvider, Provider<FamilyManager> familyManagerProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.taskDaoProvider = taskDaoProvider;
    this.goalDaoProvider = goalDaoProvider;
    this.shoppingDaoProvider = shoppingDaoProvider;
    this.familyManagerProvider = familyManagerProvider;
  }

  @Override
  public FamilySyncRepository get() {
    return newInstance(firestoreProvider.get(), authRepositoryProvider.get(), transactionDaoProvider.get(), categoryDaoProvider.get(), taskDaoProvider.get(), goalDaoProvider.get(), shoppingDaoProvider.get(), familyManagerProvider.get());
  }

  public static FamilySyncRepository_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<TaskDao> taskDaoProvider, Provider<GoalDao> goalDaoProvider,
      Provider<ShoppingDao> shoppingDaoProvider, Provider<FamilyManager> familyManagerProvider) {
    return new FamilySyncRepository_Factory(firestoreProvider, authRepositoryProvider, transactionDaoProvider, categoryDaoProvider, taskDaoProvider, goalDaoProvider, shoppingDaoProvider, familyManagerProvider);
  }

  public static FamilySyncRepository newInstance(FirebaseFirestore firestore,
      FirebaseAuthRepository authRepository, TransactionDao transactionDao, CategoryDao categoryDao,
      TaskDao taskDao, GoalDao goalDao, ShoppingDao shoppingDao, FamilyManager familyManager) {
    return new FamilySyncRepository(firestore, authRepository, transactionDao, categoryDao, taskDao, goalDao, shoppingDao, familyManager);
  }
}
