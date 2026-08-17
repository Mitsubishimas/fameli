package com.fameli.budget.di;

import com.fameli.budget.data.local.FameliDatabase;
import com.fameli.budget.data.local.dao.ShoppingDao;
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
public final class DatabaseModule_ProvideShoppingDaoFactory implements Factory<ShoppingDao> {
  private final Provider<FameliDatabase> dbProvider;

  public DatabaseModule_ProvideShoppingDaoFactory(Provider<FameliDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ShoppingDao get() {
    return provideShoppingDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideShoppingDaoFactory create(
      Provider<FameliDatabase> dbProvider) {
    return new DatabaseModule_ProvideShoppingDaoFactory(dbProvider);
  }

  public static ShoppingDao provideShoppingDao(FameliDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideShoppingDao(db));
  }
}
