package com.microsol.bancamovil.data.repository

import com.microsol.bancamovil.data.local.preferences.UserPreferences
import com.microsol.bancamovil.data.remote.api.MovementsService
import com.microsol.bancamovil.data.remote.dto.toDomain
import com.microsol.bancamovil.domain.model.Transaction
import com.microsol.bancamovil.domain.repository.MovementsRepository
import com.microsol.bancamovil.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovementsRepositoryImpl @Inject constructor(
    private val movementsService: MovementsService,
    private val userPreferences: UserPreferences
) : MovementsRepository {

    override suspend fun getAccountMovements(accountId: String): Result<List<Transaction>> =
        withContext(Dispatchers.IO) {
            try {
                val accessToken = userPreferences.getAccessToken().first()
                    ?: return@withContext Result.Error(Exception("No hay sesión activa"))

                val response = movementsService.getAccountMovements(accountId, "Bearer $accessToken")

                if (response.isSuccessful) {
                    val movementsResponse = response.body()

                    when {
                        movementsResponse?.data != null ->
                            Result.Success(movementsResponse.data.toDomain())
                        movementsResponse?.error != null ->
                            Result.Error(Exception(movementsResponse.error.userMessage.spanish))
                        else -> Result.Error(Exception("No se pudieron obtener los movimientos"))
                    }
                } else {
                    Result.Error(Exception("Error de conexión: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.Error(Exception("Error al obtener movimientos: ${e.message}"))
            }
        }
}


