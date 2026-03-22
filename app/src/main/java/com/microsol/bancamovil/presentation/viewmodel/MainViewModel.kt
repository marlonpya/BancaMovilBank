package com.microsol.bancamovil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsol.bancamovil.domain.usecase.LogoutUseCase
import com.microsol.bancamovil.domain.usecase.SessionState
import com.microsol.bancamovil.domain.usecase.ValidateSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val validateSessionUseCase: ValidateSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeSessionState()
    }

    private fun observeSessionState() {
        viewModelScope.launch {
            validateSessionUseCase().collect { sessionState ->
                when (sessionState) {
                    SessionState.Valid -> {
                        _uiState.value = _uiState.value.copy(
                            isSessionValid = true,
                            shouldNavigateToLogin = false,
                            showSessionExpiredDialog = false
                        )
                    }
                    SessionState.Expired -> {
                        _uiState.value = _uiState.value.copy(
                            isSessionValid = false,
                            shouldNavigateToLogin = false,
                            showSessionExpiredDialog = true
                        )
                    }
                    SessionState.NotLoggedIn -> {
                        _uiState.value = _uiState.value.copy(
                            isSessionValid = false,
                            shouldNavigateToLogin = true,
                            showSessionExpiredDialog = false
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun dismissSessionExpiredDialog() {
        _uiState.value = _uiState.value.copy(showSessionExpiredDialog = false)
        logout()
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


