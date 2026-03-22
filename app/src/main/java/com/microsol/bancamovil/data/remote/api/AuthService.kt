package com.microsol.bancamovil.data.remote.api

import com.microsol.bancamovil.data.remote.dto.LoginRequestDto
import com.microsol.bancamovil.data.remote.dto.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    
    @POST("auth/users/login/anonymous")
    suspend fun login(
        @Header("Authorization") authorization: String = "Basic cHJ1ZWJhc2RldjpwcnVlYmFzZGV2U2VjcmV0",
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>
}

