package com.microsol.bancamovil.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankAccount(
    val id: String,
    val accountNumber: String,
    val accountType: AccountType,
    val balance: Double,
    val currency: Currency,
    val isActive: Boolean = true
) : Parcelable

@Parcelize
enum class AccountType : Parcelable {
    SAVINGS,
    USD_SAVINGS,
}

@Parcelize
enum class Currency(val symbol: String, val code: String) : Parcelable {
    SOLES("S/", "PEN"),
    DOLLARS("US$", "USD")
}
