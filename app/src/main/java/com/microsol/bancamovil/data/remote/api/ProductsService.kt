package com.microsol.bancamovil.data.remote.api

import com.microsol.bancamovil.data.remote.dto.ProductsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ProductsService {
    
    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String
    ): Response<ProductsResponseDto>
    
    @GET("products/refresh")
    suspend fun refreshProducts(
        @Header("Authorization") authorization: String
    ): Response<ProductsResponseDto>
}

