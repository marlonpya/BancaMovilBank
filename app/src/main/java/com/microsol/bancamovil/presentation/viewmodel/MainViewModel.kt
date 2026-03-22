package com.microsol.bancamovil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsol.bancamovil.domain.repository.AuthRepository
import com.microsol.bancamovil.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            authRepository.isSessionExpired()
                .collect { isExpired ->
                    if (isExpired) {
                        _uiState.value = _uiState.value.copy(
                            isSessionValid = false,
                            showSessionExpiredDialog = true
                        )
                    }
                }
        }
    }

    fun dismissSessionExpiredDialog() {
        _uiState.value = _uiState.value.copy(showSessionExpiredDialog = false)
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = _uiState.value.copy(shouldNavigateToLogin = true)
        }
    }

    fun onNavigatedToLogin() {
        _uiState.value = _uiState.value.copy(shouldNavigateToLogin = false)
    }
}

data class MainUiState(
    val isSessionValid: Boolean = true,
    val shouldNavigateToLogin: Boolean = false,
    val showSessionExpiredDialog: Boolean = false
)
