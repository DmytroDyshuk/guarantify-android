package com.guarantify.domain.repository

import com.guarantify.domain.model.auth.AuthResult
import com.guarantify.domain.model.auth.AuthState
import com.guarantify.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface GoogleAuthRepository {
    fun observeAuthState(): Flow<AuthState>
    suspend fun signInWithGoogle(idToken: String): AuthResult?
    suspend fun signOut()
    fun getSignedUser(): UserData?
}