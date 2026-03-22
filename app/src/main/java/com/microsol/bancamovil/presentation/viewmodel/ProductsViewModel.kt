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
            loadError = false,
            showLoadErrorDialog = false
        )

        viewModelScope.launch {
            when (val result = getProductsUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = result.data,
                        loadError = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = emptyList(),
                        loadError = true,
                        showLoadErrorDialog = true
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun refreshProducts() {
        if (_uiState.value.isRefreshing) return

        _uiState.value = _uiState.value.copy(
            isRefreshing = true,
            refreshError = false,
            showRefreshErrorDialog = false
        )

        viewModelScope.launch {
            when (val result = refreshProductsUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        products = result.data,
                        refreshError = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        products = emptyList(),
                        refreshError = true,
                        showRefreshErrorDialog = true
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun dismissLoadErrorDialog() {
        _uiState.value = _uiState.value.copy(showLoadErrorDialog = false)
    }

    fun dismissRefreshErrorDialog() {
        _uiState.value = _uiState.value.copy(showRefreshErrorDialog = false)
    }
}

data class ProductsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val products: List<BankAccount> = emptyList(),
    val loadError: Boolean = false,
    val refreshError: Boolean = false,
    val showLoadErrorDialog: Boolean = false,
    val showRefreshErrorDialog: Boolean = false
)
