package com.fameli.budget.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(private val auth: FirebaseAuth) {
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init { auth.addAuthStateListener { _currentUser.value = it.currentUser } }

    fun getUserName(): String {
        val user = auth.currentUser ?: return "Я"
        return when {
            user.displayName.isNullOrBlank().not() -> user.displayName!!
            user.email.isNullOrBlank().not() -> user.email!!.substringBefore("@")
            else -> "Пользователь"
        }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> = try {
        Result.success(auth.signInWithEmailAndPassword(email, password).await().user!!)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> = try {
        Result.success(auth.createUserWithEmailAndPassword(email, password).await().user!!)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun signInAnonymously(): Result<FirebaseUser> = try {
        Result.success(auth.signInAnonymously().await().user!!)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun resetPassword(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    fun signOut() { auth.signOut() }
    fun getUserId(): String? = auth.currentUser?.uid
}
