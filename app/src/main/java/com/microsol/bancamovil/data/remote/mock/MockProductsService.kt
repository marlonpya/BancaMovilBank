package com.microsol.bancamovil.data.remote.mock

import com.microsol.bancamovil.data.remote.api.ProductsService
import com.microsol.bancamovil.data.remote.dto.ProductDto
import com.microsol.bancamovil.data.remote.dto.ProductsResponseDto
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockProductsService @Inject constructor() : ProductsService {
    
    private val mockProducts = listOf(
        ProductDto(
            id = "1",
            accountNumber = "9999999999",
            accountType = "savings",
            balance = 1000.80,
            currency = "PEN",
            isActive = true
        ),
        ProductDto(
            id = "2",
            accountNumber = "8888888888",
            accountType = "usd_savings",
            balance = 1500.20,
            currency = "USD",
            isActive = true
        ),
        ProductDto(
            id = "3",
            accountNumber = "7777777777",
            accountType = "savings",
            balance = 0.00,
            currency = "PEN",
            isActive = true
        )
    )
    
    override suspend fun getProducts(authorization: String): Response<ProductsResponseDto> {
        delay(3000)
        
        val response = ProductsResponseDto(
            data = mockProducts
        )
        
        return Response.success(response)
    }
    
    override suspend fun refreshProducts(authorization: String): Response<ProductsResponseDto> {
        delay(3000)
        
        val refreshedProducts = mockProducts.map { product ->
            product.copy(
                balance = product.balance + (Math.random() * 100 - 50)
            )
        }
        
        val response = ProductsResponseDto(
            data = refreshedProducts
        )
        
        return Response.success(response)
    }
}


