package com.fameli.budget.ui.screens.settings;

import android.content.Context;
import com.fameli.budget.firebase.FirebaseAuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<FirebaseAuthRepository> authRepositoryProvider;

  public SettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(contextProvider.get(), authRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    return new SettingsViewModel_Factory(contextProvider, authRepositoryProvider);
  }

  public static SettingsViewModel newInstance(Context context,
      FirebaseAuthRepository authRepository) {
    return new SettingsViewModel(context, authRepository);
  }
}
