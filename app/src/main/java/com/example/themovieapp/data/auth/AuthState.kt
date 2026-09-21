package com.example.themovieapp.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await


sealed interface AuthState {
    data object Loading : AuthState
    data object SignedOut : AuthState
    data class SignedIn(val user: AuthUser) : AuthState
}

data class AuthUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
)

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            trySend(
                if (user == null) AuthState.SignedOut
                else AuthState.SignedIn(user.toAuthUser())
            )
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signIn(email: String, password: String): Result<AuthUser> = try {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        Result.success(result.user!!.toAuthUser())
    } catch (e: Exception) { Result.failure(e) }

    suspend fun signUp(name: String, email: String, password: String): Result<AuthUser> = try {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = result.user!!
        if (name.isNotBlank()) {
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build()).await()
        }
        Result.success(auth.currentUser!!.toAuthUser())
    } catch (e: Exception) { Result.failure(e) }

    suspend fun sendPasswordReset(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email.trim()).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateDisplayName(name: String): Result<Unit> = try {
        val user = auth.currentUser ?: error("Not signed in")
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build()).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    fun signOut() = auth.signOut()
}

private fun FirebaseUser.toAuthUser() = AuthUser(
    uid = uid,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl?.toString(),
)