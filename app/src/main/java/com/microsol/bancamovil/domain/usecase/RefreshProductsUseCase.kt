package com.microsol.bancamovil.domain.usecase

import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.repository.ProductsRepository
import com.microsol.bancamovil.domain.util.Result
import javax.inject.Inject

class RefreshProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository
) {
    suspend operator fun invoke(): Result<List<BankAccount>> {
        return productsRepository.refreshProducts()
    }
}


