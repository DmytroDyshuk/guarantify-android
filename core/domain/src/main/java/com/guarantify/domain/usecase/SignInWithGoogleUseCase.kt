package com.guarantify.domain.usecase

import com.guarantify.domain.model.auth.AuthResult
import com.guarantify.domain.repository.GoogleAuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult? =
        googleAuthRepository.signInWithGoogle(idToken)
}