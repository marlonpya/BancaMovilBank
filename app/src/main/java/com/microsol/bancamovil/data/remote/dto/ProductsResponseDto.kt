package com.microsol.bancamovil.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.microsol.bancamovil.domain.model.AccountType
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.model.Currency

data class ProductsResponseDto(
    @SerializedName("data")
    val data: List<ProductDto>? = null,
    @SerializedName("error")
    val error: ErrorDto? = null
)

data class ProductDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("accountNumber")
    val accountNumber: String,
    @SerializedName("accountType")
    val accountType: String,
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("isActive")
    val isActive: Boolean = true
)

// Extension function para mapear DTO a modelo de dominio
fun ProductDto.toDomain(): BankAccount {
    return BankAccount(
        id = id,
        accountNumber = accountNumber,
        accountType = when (accountType.lowercase()) {
            "savings", "ahorros" -> AccountType.SAVINGS
            "usd_savings", "dolares" -> AccountType.USD_SAVINGS
            else -> AccountType.SAVINGS
        },
        balance = balance,
        currency = when (currency.uppercase()) {
            "PEN", "SOLES" -> Currency.SOLES
            "USD", "DOLLARS" -> Currency.DOLLARS
            else -> Currency.SOLES
        },
        isActive = isActive
    )
}

fun List<ProductDto>.toDomain(): List<BankAccount> {
    return this.map { it.toDomain() }
}


