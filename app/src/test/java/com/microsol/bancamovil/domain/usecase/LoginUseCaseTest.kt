package com.microsol.bancamovil.domain.usecase

import com.microsol.bancamovil.domain.repository.AuthRepository
import com.microsol.bancamovil.domain.util.Result
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginUseCaseTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
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


