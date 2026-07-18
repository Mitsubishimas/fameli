package com.fameli.budget.firebase;

import com.google.firebase.auth.FirebaseAuth;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FirebaseAuthRepository_Factory implements Factory<FirebaseAuthRepository> {
  private final Provider<FirebaseAuth> authProvider;

  public FirebaseAuthRepository_Factory(Provider<FirebaseAuth> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public FirebaseAuthRepository get() {
    return newInstance(authProvider.get());
  }

  public static FirebaseAuthRepository_Factory create(Provider<FirebaseAuth> authProvider) {
    return new FirebaseAuthRepository_Factory(authProvider);
  }

  public static FirebaseAuthRepository newInstance(FirebaseAuth auth) {
    return new FirebaseAuthRepository(auth);
  }
}
