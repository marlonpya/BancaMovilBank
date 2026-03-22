package com.microsol.bancamovil.data.remote.mock

import com.microsol.bancamovil.data.remote.api.ProductsService
import com.microsol.bancamovil.data.remote.dto.ProductDto
import com.microsol.bancamovil.data.remote.dto.ProductsResponseDto
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockProductsService @Inject constructor() : ProductsService {

    var simulateGetError = false
    var simulateRefreshError = false

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
            balance = 1800.20,
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
        if (simulateGetError) {
            simulateGetError = false
            return Response.error(
                500,
                """{"error":"Error al obtener cuentas"}""".toResponseBody("application/json".toMediaType())
            )
        }
        return Response.success(ProductsResponseDto(data = mockProducts))
    }

    override suspend fun refreshProducts(authorization: String): Response<ProductsResponseDto> {
        delay(3000)
        if (simulateRefreshError) {
            simulateRefreshError = false
            return Response.error(
                500,
                """{"error":"Error al actualizar cuentas"}""".toResponseBody("application/json".toMediaType())
            )
        }
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
