package com.guarantify.domain.usecase

import com.guarantify.domain.model.auth.AuthResult
import com.guarantify.domain.repository.AuthRepository
import jakarta.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult? =
        authRepository.signInWithGoogle(idToken)
}