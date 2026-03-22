package com.microsol.bancamovil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsol.bancamovil.domain.repository.AuthRepository
import com.microsol.bancamovil.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SESSION_DURATION_MS = 2 * 60 * 1000L
private const val CHECK_INTERVAL_MS = 1_000L

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        startSessionPolling()
    }

    private fun startSessionPolling() {
        viewModelScope.launch {
            while (true) {
                delay(CHECK_INTERVAL_MS)

                val isLoggedIn = authRepository.isLoggedIn().first()
                if (!isLoggedIn) break

                val timestamp = authRepository.getLoginTimestamp() ?: break
                val elapsed = System.currentTimeMillis() - timestamp

                if (elapsed > SESSION_DURATION_MS) {
                    _uiState.value = _uiState.value.copy(
                        isSessionValid = false,
                        showSessionExpiredDialog = true
                    )
                    break
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
        _uiState.value = _uiState.value.copy(shouldNavigateToLogin = true)
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
