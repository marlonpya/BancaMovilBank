package com.microsol.bancamovil.domain.repository

import com.microsol.bancamovil.domain.model.Transaction
import com.microsol.bancamovil.domain.util.Result

interface MovementsRepository {
    suspend fun getAccountMovements(accountId: String): Result<List<Transaction>>
}


