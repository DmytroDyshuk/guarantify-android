package com.guarantify.domain.repository

import com.guarantify.domain.model.AuthResult
import com.guarantify.domain.model.UserData

interface GoogleAuthRepository {
    suspend fun signInWithGoogle(idToken: String): AuthResult?
    suspend fun signOut()
    fun getSignedUser(): UserData?
}