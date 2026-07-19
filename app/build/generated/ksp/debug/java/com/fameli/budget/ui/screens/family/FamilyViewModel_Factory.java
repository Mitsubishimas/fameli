package com.fameli.budget.ui.screens.family;

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

  public FamilyViewModel_Factory(Provider<FamilySyncRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public FamilyViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static FamilyViewModel_Factory create(Provider<FamilySyncRepository> repoProvider) {
    return new FamilyViewModel_Factory(repoProvider);
  }

  public static FamilyViewModel newInstance(FamilySyncRepository repo) {
    return new FamilyViewModel(repo);
  }
}
