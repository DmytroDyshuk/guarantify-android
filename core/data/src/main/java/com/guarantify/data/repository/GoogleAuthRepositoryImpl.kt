package com.guarantify.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.guarantify.domain.model.AuthResult
import com.guarantify.domain.model.AuthState
import com.guarantify.domain.model.UserData
import com.guarantify.domain.repository.GoogleAuthRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : GoogleAuthRepository {

    override fun observeAuthState(): Flow<AuthState> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(
                element = if (auth.currentUser != null) {
                    AuthState.Authenticated
                } else AuthState.Unauthenticated
            )
        }

        firebaseAuth.addAuthStateListener(authListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authListener)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val user = firebaseAuth.signInWithCredential(credential).await().user
            AuthResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AuthResult(data = null, errorMessage = e.message)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getSignedUser(): UserData? =
        firebaseAuth.currentUser?.run {
            UserData(
                userId = uid,
                username = displayName,
                profilePictureUrl = photoUrl?.toString()
            )
        }

}