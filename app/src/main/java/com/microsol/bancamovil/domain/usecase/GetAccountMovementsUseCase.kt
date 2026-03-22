package com.microsol.bancamovil.domain.usecase

import com.microsol.bancamovil.domain.model.Transaction
import com.microsol.bancamovil.domain.repository.MovementsRepository
import com.microsol.bancamovil.domain.util.Result
import javax.inject.Inject

class GetAccountMovementsUseCase @Inject constructor(
    private val movementsRepository: MovementsRepository
) {
    suspend operator fun invoke(accountId: String): Result<List<Transaction>> {
        if (accountId.isBlank()) {
            return Result.Error(Exception("ID de cuenta requerido"))
        }
        
        return movementsRepository.getAccountMovements(accountId)
    }
}


