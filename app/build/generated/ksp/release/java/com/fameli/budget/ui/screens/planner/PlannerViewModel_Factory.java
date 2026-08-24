package com.fameli.budget.ui.screens.planner;

import com.fameli.budget.data.local.dao.TaskDao;
import com.fameli.budget.data.repository.FamilyManager;
import com.fameli.budget.data.repository.FamilySyncRepository;
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
public final class PlannerViewModel_Factory implements Factory<PlannerViewModel> {
  private final Provider<TaskDao> taskDaoProvider;

  private final Provider<FirebaseAuthRepository> authRepositoryProvider;

  private final Provider<FamilySyncRepository> familyRepoProvider;

  private final Provider<FamilyManager> familyManagerProvider;

  public PlannerViewModel_Factory(Provider<TaskDao> taskDaoProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider,
      Provider<FamilySyncRepository> familyRepoProvider,
      Provider<FamilyManager> familyManagerProvider) {
    this.taskDaoProvider = taskDaoProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.familyRepoProvider = familyRepoProvider;
    this.familyManagerProvider = familyManagerProvider;
  }

  @Override
  public PlannerViewModel get() {
    return newInstance(taskDaoProvider.get(), authRepositoryProvider.get(), familyRepoProvider.get(), familyManagerProvider.get());
  }

  public static PlannerViewModel_Factory create(Provider<TaskDao> taskDaoProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider,
      Provider<FamilySyncRepository> familyRepoProvider,
      Provider<FamilyManager> familyManagerProvider) {
    return new PlannerViewModel_Factory(taskDaoProvider, authRepositoryProvider, familyRepoProvider, familyManagerProvider);
  }

  public static PlannerViewModel newInstance(TaskDao taskDao, FirebaseAuthRepository authRepository,
      FamilySyncRepository familyRepo, FamilyManager familyManager) {
    return new PlannerViewModel(taskDao, authRepository, familyRepo, familyManager);
  }
}
