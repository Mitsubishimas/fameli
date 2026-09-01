package com.fameli.budget.ui.screens.categories;

import com.fameli.budget.data.local.dao.CategoryDao;
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

  public CategoriesViewModel_Factory(Provider<CategoryDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public CategoriesViewModel get() {
    return newInstance(daoProvider.get());
  }

  public static CategoriesViewModel_Factory create(Provider<CategoryDao> daoProvider) {
    return new CategoriesViewModel_Factory(daoProvider);
  }

  public static CategoriesViewModel newInstance(CategoryDao dao) {
    return new CategoriesViewModel(dao);
  }
}
