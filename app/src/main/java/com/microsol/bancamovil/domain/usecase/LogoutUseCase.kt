package com.microsol.bancamovil.domain.usecase

import com.microsol.bancamovil.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.clearSession()
    }
}


