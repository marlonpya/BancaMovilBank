package com.microsol.bancamovil.domain.repository

import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.util.Result

interface ProductsRepository {
    suspend fun getProducts(): Result<List<BankAccount>>
    suspend fun refreshProducts(): Result<List<BankAccount>>
    suspend fun getProductById(id: String): BankAccount?
}

