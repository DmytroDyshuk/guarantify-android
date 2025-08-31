package com.guarantify.domain.usecase

import com.guarantify.domain.model.AuthResult
import com.guarantify.domain.repository.GoogleAuthRepository
import javax.inject.Inject

class GoogleAuthUserUseCase @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult? =
        googleAuthRepository.signInWithGoogle(idToken)
}