package com.guarantify.domain.usecase

import com.guarantify.common.result.Result
import com.guarantify.domain.repository.AuthRepository
import com.guarantify.domain.repository.LocalDataRepository
import jakarta.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localDataRepository: LocalDataRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val clearResult = localDataRepository.clearLocalData()
        if (clearResult is Result.Error) return clearResult

        authRepository.signOut()
        return Result.Success(Unit)
    }
}
