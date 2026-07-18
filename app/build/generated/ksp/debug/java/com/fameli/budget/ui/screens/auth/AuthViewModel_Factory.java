package com.fameli.budget.ui.screens.auth;

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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<FirebaseAuthRepository> authRepositoryProvider;

  public AuthViewModel_Factory(Provider<FirebaseAuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static AuthViewModel_Factory create(
      Provider<FirebaseAuthRepository> authRepositoryProvider) {
    return new AuthViewModel_Factory(authRepositoryProvider);
  }

  public static AuthViewModel newInstance(FirebaseAuthRepository authRepository) {
    return new AuthViewModel(authRepository);
  }
}
