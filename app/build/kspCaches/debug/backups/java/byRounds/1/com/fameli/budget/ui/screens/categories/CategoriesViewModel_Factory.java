package com.fameli.budget.ui.screens.categories;

import com.fameli.budget.data.local.dao.CategoryDao;
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
public final class CategoriesViewModel_Factory implements Factory<CategoriesViewModel> {
  private final Provider<CategoryDao> daoProvider;

  private final Provider<FamilySyncRepository> familyRepoProvider;

  public CategoriesViewModel_Factory(Provider<CategoryDao> daoProvider,
      Provider<FamilySyncRepository> familyRepoProvider) {
    this.daoProvider = daoProvider;
    this.familyRepoProvider = familyRepoProvider;
  }

  @Override
  public CategoriesViewModel get() {
    return newInstance(daoProvider.get(), familyRepoProvider.get());
  }

  public static CategoriesViewModel_Factory create(Provider<CategoryDao> daoProvider,
      Provider<FamilySyncRepository> familyRepoProvider) {
    return new CategoriesViewModel_Factory(daoProvider, familyRepoProvider);
  }

  public static CategoriesViewModel newInstance(CategoryDao dao, FamilySyncRepository familyRepo) {
    return new CategoriesViewModel(dao, familyRepo);
  }
}
