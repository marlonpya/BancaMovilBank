package com.microsol.bancamovil.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsol.bancamovil.domain.usecase.LoginUseCase
import com.microsol.bancamovil.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel(), LoginScreenViewModel {

    private val _formState = MutableStateFlow(LoginFormState())
    override val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    private val _authState = MutableStateFlow<LoginAuthState>(LoginAuthState.Idle)
    override val authState: StateFlow<LoginAuthState> = _authState.asStateFlow()

    override fun updateUsername(username: String) {
        _formState.value = _formState.value.copy(username = username)
    }

    override fun updatePassword(password: String) {
        _formState.value = _formState.value.copy(password = password)
    }

    override fun login() {
        if (_authState.value is LoginAuthState.Loading) return

        _authState.value = LoginAuthState.Loading

        viewModelScope.launch {
            val form = _formState.value
            when (val result = loginUseCase(form.username, form.password)) {
                is Result.Success -> _authState.value = LoginAuthState.Success
                is Result.Error -> _authState.value = LoginAuthState.Error(
                    result.exception.message ?: "Error desconocido"
                )
            }
        }
    }

    override fun clearError() {
        if (_authState.value is LoginAuthState.Error) {
            _authState.value = LoginAuthState.Idle
        }
    }

    override fun resetSuccessState() {
        _authState.value = LoginAuthState.Idle
    }
}

data class LoginFormState(
    val username: String = "",
    val password: String = ""
)

sealed class LoginAuthState {
    object Idle : LoginAuthState()
    object Loading : LoginAuthState()
    object Success : LoginAuthState()
    data class Error(val message: String) : LoginAuthState()
}

interface LoginScreenViewModel {
    val formState: StateFlow<LoginFormState>
    val authState: StateFlow<LoginAuthState>
    fun updateUsername(username: String)
    fun updatePassword(password: String)
    fun login()
    fun clearError()
    fun resetSuccessState()
}
