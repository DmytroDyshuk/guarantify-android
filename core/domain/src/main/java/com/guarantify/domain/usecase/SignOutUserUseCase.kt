package com.guarantify.domain.usecase

import com.guarantify.domain.repository.GoogleAuthRepository
import javax.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository
) {
    suspend operator fun invoke() {
        googleAuthRepository.signOut()
    }
}