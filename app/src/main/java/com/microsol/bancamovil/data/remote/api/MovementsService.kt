package com.microsol.bancamovil.data.remote.api

import com.microsol.bancamovil.data.remote.dto.MovementsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MovementsService {
    
    @GET("accounts/{accountId}/movements")
    suspend fun getAccountMovements(
        @Path("accountId") accountId: String,
        @Header("Authorization") authorization: String
    ): Response<MovementsResponseDto>
}

