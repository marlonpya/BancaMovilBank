package com.microsol.bancamovil.domain.model

data class BankAccount(
    val id: String,
    val accountNumber: String,
    val accountType: AccountType,
    val balance: Double,
    val currency: Currency,
    val isActive: Boolean = true
)

enum class AccountType {
    SAVINGS,
    USD_SAVINGS,
}

enum class Currency(val symbol: String, val code: String) {
    SOLES("S/", "PEN"),
    DOLLARS("US$", "USD")
}
