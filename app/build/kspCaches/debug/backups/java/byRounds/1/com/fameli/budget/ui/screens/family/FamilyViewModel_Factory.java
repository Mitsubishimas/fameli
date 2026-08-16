package com.fameli.budget.ui.screens.family;

import com.fameli.budget.data.repository.FamilyManager;
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
public final class FamilyViewModel_Factory implements Factory<FamilyViewModel> {
  private final Provider<FamilySyncRepository> repoProvider;

  private final Provider<FamilyManager> familyManagerProvider;

  public FamilyViewModel_Factory(Provider<FamilySyncRepository> repoProvider,
      Provider<FamilyManager> familyManagerProvider) {
    this.repoProvider = repoProvider;
    this.familyManagerProvider = familyManagerProvider;
  }

  @Override
  public FamilyViewModel get() {
    return newInstance(repoProvider.get(), familyManagerProvider.get());
  }

  public static FamilyViewModel_Factory create(Provider<FamilySyncRepository> repoProvider,
      Provider<FamilyManager> familyManagerProvider) {
    return new FamilyViewModel_Factory(repoProvider, familyManagerProvider);
  }

  public static FamilyViewModel newInstance(FamilySyncRepository repo,
      FamilyManager familyManager) {
    return new FamilyViewModel(repo, familyManager);
  }
}
