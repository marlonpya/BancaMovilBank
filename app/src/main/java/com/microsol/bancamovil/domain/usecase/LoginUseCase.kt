package com.microsol.bancamovil.domain.usecase

import com.microsol.bancamovil.domain.repository.AuthRepository
import com.microsol.bancamovil.domain.util.Result
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> {
        // Validaciones básicas
        if (username.isBlank()) {
            return Result.Error(Exception("El usuario es requerido"))
        }
        
        if (password.isBlank()) {
            return Result.Error(Exception("La contraseña es requerida"))
        }
        
        // Intentar login
        return when (val result = authRepository.login(username, password)) {
            is Result.Success -> {
                authRepository.saveSession(result.data)
                Result.Success(Unit)
            }
            is Result.Error -> result
        }
    }
}
