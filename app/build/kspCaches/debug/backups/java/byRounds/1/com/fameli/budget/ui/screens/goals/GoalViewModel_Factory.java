package com.fameli.budget.ui.screens.goals;

import com.fameli.budget.data.local.dao.GoalDao;
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
public final class GoalViewModel_Factory implements Factory<GoalViewModel> {
  private final Provider<GoalDao> goalDaoProvider;

  private final Provider<FirebaseAuthRepository> authRepositoryProvider;

  public GoalViewModel_Factory(Provider<GoalDao> goalDaoProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    this.goalDaoProvider = goalDaoProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public GoalViewModel get() {
    return newInstance(goalDaoProvider.get(), authRepositoryProvider.get());
  }

  public static GoalViewModel_Factory create(Provider<GoalDao> goalDaoProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    return new GoalViewModel_Factory(goalDaoProvider, authRepositoryProvider);
  }

  public static GoalViewModel newInstance(GoalDao goalDao, FirebaseAuthRepository authRepository) {
    return new GoalViewModel(goalDao, authRepository);
  }
}
