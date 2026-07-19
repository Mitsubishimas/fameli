package com.fameli.budget.di;

import com.fameli.budget.data.repository.FamilySyncRepository;
import com.fameli.budget.ui.screens.family.FamilyViewModel;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class FamilyModule_ProvideFamilyViewModelFactory implements Factory<FamilyViewModel> {
  private final Provider<FamilySyncRepository> repoProvider;

  public FamilyModule_ProvideFamilyViewModelFactory(Provider<FamilySyncRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public FamilyViewModel get() {
    return provideFamilyViewModel(repoProvider.get());
  }

  public static FamilyModule_ProvideFamilyViewModelFactory create(
      Provider<FamilySyncRepository> repoProvider) {
    return new FamilyModule_ProvideFamilyViewModelFactory(repoProvider);
  }

  public static FamilyViewModel provideFamilyViewModel(FamilySyncRepository repo) {
    return Preconditions.checkNotNullFromProvides(FamilyModule.INSTANCE.provideFamilyViewModel(repo));
  }
}
