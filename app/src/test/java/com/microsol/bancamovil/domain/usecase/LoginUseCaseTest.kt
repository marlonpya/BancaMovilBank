package com.microsol.bancamovil.domain.usecase

import com.microsol.bancamovil.domain.repository.AuthRepository
import com.microsol.bancamovil.domain.util.Result
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `login with empty username should return error`() = runTest {
        // Given
        val username = ""
        val password = "password123"

        // When
        val result = loginUseCase(username, password)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("El usuario es requerido", result.exception.message)
    }

    @Test
    fun `login with empty password should return error`() = runTest {
        // Given
        val username = "user123"
        val password = ""

        // When
        val result = loginUseCase(username, password)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("La contraseña es requerida", result.exception.message)
    }

}


