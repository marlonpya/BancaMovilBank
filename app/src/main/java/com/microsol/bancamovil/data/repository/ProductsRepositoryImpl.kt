package com.microsol.bancamovil.data.repository

import com.microsol.bancamovil.data.local.preferences.UserPreferences
import com.microsol.bancamovil.data.remote.api.ProductsService
import com.microsol.bancamovil.data.remote.dto.toDomain
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.repository.ProductsRepository
import com.microsol.bancamovil.domain.util.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepositoryImpl @Inject constructor(
    private val productsService: ProductsService,
    private val userPreferences: UserPreferences
) : ProductsRepository {

    override suspend fun getProducts(): Result<List<BankAccount>> {
        return try {
            val accessToken = userPreferences.getAccessToken().first()
            if (accessToken == null) {
                return Result.Error(Exception("No hay sesión activa"))
            }

            val response = productsService.getProducts("Bearer $accessToken")
            
            if (response.isSuccessful) {
                val productsResponse = response.body()
                
                when {
                    productsResponse?.data != null -> {
                        val products = productsResponse.data.toDomain()
                        Result.Success(products)
                    }
                    productsResponse?.error != null -> {
                        val errorMessage = productsResponse.error.userMessage.spanish
                        Result.Error(Exception(errorMessage))
                    }
                    else -> {
                        Result.Error(Exception("No se pudieron obtener los productos"))
                    }
                }
            } else {
                Result.Error(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Error al obtener productos: ${e.message}"))
        }
    }

    override suspend fun refreshProducts(): Result<List<BankAccount>> {
        return try {
            val accessToken = userPreferences.getAccessToken().first()
            if (accessToken == null) {
                return Result.Error(Exception("No hay sesión activa"))
            }

            val response = productsService.refreshProducts("Bearer $accessToken")
            
            if (response.isSuccessful) {
                val productsResponse = response.body()
                
                when {
                    productsResponse?.data != null -> {
                        val products = productsResponse.data.toDomain()
                        Result.Success(products)
                    }
                    productsResponse?.error != null -> {
                        val errorMessage = productsResponse.error.userMessage.spanish
                        Result.Error(Exception(errorMessage))
                    }
                    else -> {
                        Result.Error(Exception("No se pudieron actualizar los productos"))
                    }
                }
            } else {
                Result.Error(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Error al actualizar productos: ${e.message}"))
        }
    }
}


