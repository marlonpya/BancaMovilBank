package com.microsol.bancamovil.domain.usecase

import com.microsol.bancamovil.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ValidateSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<SessionState> {
        return combine(
            authRepository.isLoggedIn(),
            authRepository.isSessionExpired()
        ) { isLoggedIn, isExpired ->
            when {
                !isLoggedIn -> SessionState.NotLoggedIn
                isExpired -> SessionState.Expired
                else -> SessionState.Valid
            }
        }
    }
}

enum class SessionState {
    Valid,
    Expired,
    NotLoggedIn
}


