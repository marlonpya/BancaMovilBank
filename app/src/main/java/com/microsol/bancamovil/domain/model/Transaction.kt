package com.microsol.bancamovil.domain.model

import java.util.Date

data class Transaction(
    val id: String,
    val accountId: String,
    val type: TransactionType,
    val amount: Double,
    val currency: Currency,
    val description: String,
    val date: Date,
    val balance: Double? = null,
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_IN,
    TRANSFER_OUT,
    PAYMENT,
    FEE,
    INTEREST
}


