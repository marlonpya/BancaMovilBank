package com.microsol.bancamovil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.usecase.GetProductsUseCase
import com.microsol.bancamovil.domain.usecase.RefreshProductsUseCase
import com.microsol.bancamovil.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val refreshProductsUseCase: RefreshProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        if (_uiState.value.isLoading) return
        
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            when (val result = getProductsUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Error al cargar productos"
                    )
                }
                is Result.Loading -> {
                    // Ya estamos en loading
                }
            }
        }
    }

    fun refreshProducts() {
        if (_uiState.value.isRefreshing) return
        
        _uiState.value = _uiState.value.copy(
            isRefreshing = true,
            error = null
        )

        viewModelScope.launch {
            when (val result = refreshProductsUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        products = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = result.exception.message ?: "Error al actualizar productos"
                    )
                }
                is Result.Loading -> {
                    // Ya estamos en refreshing
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ProductsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val products: List<BankAccount> = emptyList(),
    val error: String? = null
)


