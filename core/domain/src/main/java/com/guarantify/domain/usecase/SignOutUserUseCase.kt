package com.guarantify.domain.usecase

import com.guarantify.domain.repository.AuthRepository
import jakarta.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}