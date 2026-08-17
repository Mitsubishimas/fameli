package com.fameli.budget.data.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class FamilyManager_Factory implements Factory<FamilyManager> {
  private final Provider<Context> contextProvider;

  public FamilyManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FamilyManager get() {
    return newInstance(contextProvider.get());
  }

  public static FamilyManager_Factory create(Provider<Context> contextProvider) {
    return new FamilyManager_Factory(contextProvider);
  }

  public static FamilyManager newInstance(Context context) {
    return new FamilyManager(context);
  }
}
