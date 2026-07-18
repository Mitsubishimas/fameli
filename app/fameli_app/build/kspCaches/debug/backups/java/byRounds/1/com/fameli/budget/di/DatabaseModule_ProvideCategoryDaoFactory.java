package com.fameli.budget.di;

import com.fameli.budget.data.local.FameliDatabase;
import com.fameli.budget.data.local.dao.CategoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideCategoryDaoFactory implements Factory<CategoryDao> {
  private final Provider<FameliDatabase> dbProvider;

  public DatabaseModule_ProvideCategoryDaoFactory(Provider<FameliDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CategoryDao get() {
    return provideCategoryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideCategoryDaoFactory create(
      Provider<FameliDatabase> dbProvider) {
    return new DatabaseModule_ProvideCategoryDaoFactory(dbProvider);
  }

  public static CategoryDao provideCategoryDao(FameliDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCategoryDao(db));
  }
}
