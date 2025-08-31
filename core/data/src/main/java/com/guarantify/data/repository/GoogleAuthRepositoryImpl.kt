package com.guarantify.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.guarantify.domain.model.AuthResult
import com.guarantify.domain.model.UserData
import com.guarantify.domain.repository.GoogleAuthRepository

class GoogleAuthRepositoryImpl(
   private val firebaseAuth: FirebaseAuth
) : GoogleAuthRepository {
    override suspend fun getGoogleSignInIntent(idToken: String): AuthResult? {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }

    override fun getSignedUser(): UserData? {
        TODO("Not yet implemented")
    }
}