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
        _uiState.value = _uiState.value.copy(
            content = ProductsContent.Loading,
            showLoadErrorDialog = false
        )

        viewModelScope.launch {
            when (val result = getProductsUseCase()) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    content = ProductsContent.Success(result.data)
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    content = ProductsContent.LoadError,
                    showLoadErrorDialog = true
                )
            }
        }
    }

    fun refreshProducts() {
        if (_uiState.value.isRefreshing) return

        _uiState.value = _uiState.value.copy(
            isRefreshing = true,
            showRefreshErrorDialog = false
        )

        viewModelScope.launch {
            when (val result = refreshProductsUseCase()) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    content = ProductsContent.Success(result.data)
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    content = ProductsContent.RefreshError,
                    showRefreshErrorDialog = true
                )
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

sealed class ProductsContent {
    object Loading : ProductsContent()
    data class Success(val products: List<BankAccount>) : ProductsContent()
    object LoadError : ProductsContent()
    object RefreshError : ProductsContent()
}

data class ProductsUiState(
    val content: ProductsContent = ProductsContent.Loading,
    val isRefreshing: Boolean = false,
    val showLoadErrorDialog: Boolean = false,
    val showRefreshErrorDialog: Boolean = false
)
