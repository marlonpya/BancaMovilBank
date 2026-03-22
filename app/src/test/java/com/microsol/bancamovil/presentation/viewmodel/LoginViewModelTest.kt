package com.microsol.bancamovil.presentation.viewmodel

import app.cash.turbine.test
import com.microsol.bancamovil.domain.usecase.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @Mock
    private lateinit var loginUseCase: LoginUseCase

    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertFalse(initialState.isSuccess)
            assertEquals("", initialState.username)
            assertEquals("", initialState.password)
            assertEquals(null, initialState.error)
        }
    }

    @Test
    fun `updateUsername should update username in state`() = runTest {
        val newUsername = "testuser"
        
        viewModel.updateUsername(newUsername)
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(newUsername, state.username)
        }
    }

    @Test
    fun `updatePassword should update password in state`() = runTest {
        val newPassword = "testpassword"
        
        viewModel.updatePassword(newPassword)
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(newPassword, state.password)
        }
    }
}


