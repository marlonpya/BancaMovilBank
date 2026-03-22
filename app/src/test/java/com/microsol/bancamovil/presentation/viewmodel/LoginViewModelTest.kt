package com.microsol.bancamovil.presentation.viewmodel

import app.cash.turbine.test
import com.microsol.bancamovil.domain.usecase.LoginUseCase
import com.microsol.bancamovil.domain.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @MockK
    private lateinit var loginUseCase: LoginUseCase

    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial authState should be Idle`() = runTest {
        viewModel.authState.test {
            assertEquals(LoginAuthState.Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial formState should have empty fields`() = runTest {
        viewModel.formState.test {
            val form = awaitItem()
            assertEquals("", form.username)
            assertEquals("", form.password)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateUsername should update username in formState`() = runTest {
        viewModel.updateUsername("testuser")

        viewModel.formState.test {
            assertEquals("testuser", awaitItem().username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updatePassword should update password in formState`() = runTest {
        viewModel.updatePassword("testpassword")

        viewModel.formState.test {
            assertEquals("testpassword", awaitItem().password)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login success emits Loading then Success`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.Success(Unit)

        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")

        viewModel.authState.test {
            assertEquals(LoginAuthState.Idle, awaitItem())
            viewModel.login()
            assertEquals(LoginAuthState.Loading, awaitItem())
            assertEquals(LoginAuthState.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login error emits Loading then Error with message`() = runTest {
        val errorMessage = "Credenciales incorrectas"
        coEvery { loginUseCase(any(), any()) } returns Result.Error(Exception(errorMessage))

        viewModel.updateUsername("user")
        viewModel.updatePassword("wrong")

        viewModel.authState.test {
            assertEquals(LoginAuthState.Idle, awaitItem())
            viewModel.login()
            assertEquals(LoginAuthState.Loading, awaitItem())
            val errorState = awaitItem()
            assertIs<LoginAuthState.Error>(errorState)
            assertEquals(errorMessage, errorState.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `second login call while loading is blocked`() = runTest {
        val deferred = CompletableDeferred<Result<Unit>>()
        coEvery { loginUseCase(any(), any()) } coAnswers { deferred.await() }

        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")

        viewModel.authState.test {
            assertEquals(LoginAuthState.Idle, awaitItem())
            viewModel.login()
            assertEquals(LoginAuthState.Loading, awaitItem())

            viewModel.login()

            deferred.complete(Result.Success(Unit))
            assertEquals(LoginAuthState.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { loginUseCase(any(), any()) }
    }

    @Test
    fun `clearError resets authState from Error to Idle`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.Error(Exception("error"))

        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        viewModel.login()

        assertIs<LoginAuthState.Error>(viewModel.authState.value)
        viewModel.clearError()
        assertEquals(LoginAuthState.Idle, viewModel.authState.value)
    }

    @Test
    fun `resetSuccessState resets authState from Success to Idle`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.Success(Unit)

        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        viewModel.login()

        assertEquals(LoginAuthState.Success, viewModel.authState.value)
        viewModel.resetSuccessState()
        assertEquals(LoginAuthState.Idle, viewModel.authState.value)
    }
}
