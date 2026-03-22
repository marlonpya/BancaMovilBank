package com.microsol.bancamovil.domain.repository

import com.microsol.bancamovil.domain.model.AuthSession
import com.microsol.bancamovil.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthSession>
    suspend fun saveSession(session: AuthSession)
    suspend fun getSession(): Flow<AuthSession?>
    suspend fun clearSession()
    fun isLoggedIn(): Flow<Boolean>
    fun isSessionExpired(): Flow<Boolean>
}

