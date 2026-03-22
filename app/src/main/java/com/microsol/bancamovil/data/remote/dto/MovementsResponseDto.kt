package com.microsol.bancamovil.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.microsol.bancamovil.domain.model.Currency
import com.microsol.bancamovil.domain.model.Transaction
import com.microsol.bancamovil.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MovementsResponseDto(
    @SerializedName("data")
    val data: List<MovementDto>? = null,
    @SerializedName("error")
    val error: ErrorDto? = null
)

data class MovementDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("balance")
    val balance: Double? = null,
)

fun MovementDto.toDomain(): Transaction {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val parsedDate = try {
        dateFormat.parse(date) ?: Date()
    } catch (e: Exception) {
        Date()
    }

    return Transaction(
        id = id,
        accountId = accountId,
        type = when (type.lowercase()) {
            "deposit", "deposito" -> TransactionType.DEPOSIT
            "withdrawal", "retiro" -> TransactionType.WITHDRAWAL
            "transfer_in", "transferencia_recibida" -> TransactionType.TRANSFER_IN
            "transfer_out", "transferencia_enviada" -> TransactionType.TRANSFER_OUT
            "payment", "pago" -> TransactionType.PAYMENT
            "fee", "comision" -> TransactionType.FEE
            "interest", "interes" -> TransactionType.INTEREST
            else -> TransactionType.DEPOSIT
        },
        amount = amount,
        currency = when (currency.uppercase()) {
            "PEN", "SOLES" -> Currency.SOLES
            "USD", "DOLLARS" -> Currency.DOLLARS
            else -> Currency.SOLES
        },
        description = description,
        date = parsedDate,
        balance = balance,
    )
}

fun List<MovementDto>.toDomain(): List<Transaction> {
    return this.map { it.toDomain() }
}


