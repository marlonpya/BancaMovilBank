package com.microsol.bancamovil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.model.Transaction
import com.microsol.bancamovil.domain.repository.ProductsRepository
import com.microsol.bancamovil.domain.usecase.GetAccountMovementsUseCase
import com.microsol.bancamovil.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val getAccountMovementsUseCase: GetAccountMovementsUseCase,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountDetailUiState>(AccountDetailUiState.Loading)
    val uiState: StateFlow<AccountDetailUiState> = _uiState.asStateFlow()

    fun loadAccountDetail(accountId: String) {
        _uiState.value = AccountDetailUiState.Loading

        viewModelScope.launch {
            val account = productsRepository.getProductById(accountId)
            if (account == null) {
                _uiState.value = AccountDetailUiState.Error("Cuenta no encontrada")
                return@launch
            }

            when (val result = getAccountMovementsUseCase(accountId)) {
                is Result.Success -> _uiState.value =
                    if (result.data.isEmpty()) AccountDetailUiState.Empty(account)
                    else AccountDetailUiState.Success(account, result.data)
                is Result.Error -> _uiState.value = AccountDetailUiState.Error(
                    result.exception.message ?: "Error al cargar movimientos"
                )
            }
        }
    }
}

sealed class AccountDetailUiState {
    object Loading : AccountDetailUiState()
    data class Success(val account: BankAccount, val movements: List<Transaction>) : AccountDetailUiState()
    data class Empty(val account: BankAccount) : AccountDetailUiState()
    data class Error(val message: String) : AccountDetailUiState()
}
