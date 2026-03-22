package com.microsol.bancamovil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.model.Transaction
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
    private val getAccountMovementsUseCase: GetAccountMovementsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountDetailUiState())
    val uiState: StateFlow<AccountDetailUiState> = _uiState.asStateFlow()

    fun loadAccountDetail(accountId: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            when (val result = getAccountMovementsUseCase(accountId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        movements = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Error al cargar movimientos"
                    )
                }
                is Result.Loading -> {
                    // Ya estamos en loading
                }
            }
        }
    }

    fun setAccount(account: BankAccount) {
        _uiState.value = _uiState.value.copy(account = account)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AccountDetailUiState(
    val isLoading: Boolean = false,
    val account: BankAccount? = null,
    val movements: List<Transaction> = emptyList(),
    val error: String? = null
)
