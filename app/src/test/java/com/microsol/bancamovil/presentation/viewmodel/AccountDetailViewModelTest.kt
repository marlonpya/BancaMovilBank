package com.microsol.bancamovil.presentation.viewmodel

import com.microsol.bancamovil.domain.model.AccountType
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.model.Currency
import com.microsol.bancamovil.domain.model.Transaction
import com.microsol.bancamovil.domain.model.TransactionType
import com.microsol.bancamovil.domain.repository.ProductsRepository
import com.microsol.bancamovil.domain.usecase.GetAccountMovementsUseCase
import com.microsol.bancamovil.domain.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AccountDetailViewModelTest {

    @MockK
    private lateinit var getAccountMovementsUseCase: GetAccountMovementsUseCase

    @MockK
    private lateinit var productsRepository: ProductsRepository

    private lateinit var viewModel: AccountDetailViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockAccount = BankAccount(
        id = "1",
        accountNumber = "001-123456-0-01",
        accountType = AccountType.SAVINGS,
        balance = 1500.0,
        currency = Currency.SOLES
    )

    private val mockMovements = listOf(
        Transaction(
            id = "t1",
            accountId = "1",
            type = TransactionType.DEPOSIT,
            amount = 500.0,
            currency = Currency.SOLES,
            description = "Depósito",
            date = Date()
        ),
        Transaction(
            id = "t2",
            accountId = "1",
            type = TransactionType.WITHDRAWAL,
            amount = -100.0,
            currency = Currency.SOLES,
            description = "Retiro",
            date = Date()
        )
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AccountDetailViewModel(getAccountMovementsUseCase, productsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() {
        assertIs<AccountDetailUiState.Loading>(viewModel.uiState.value)
    }

    @Test
    fun `loadAccountDetail with movements emits Success`() = runTest {
        coEvery { productsRepository.getProductById("1") } returns mockAccount
        coEvery { getAccountMovementsUseCase("1") } returns Result.Success(mockMovements)

        viewModel.loadAccountDetail("1")

        val state = viewModel.uiState.value
        assertIs<AccountDetailUiState.Success>(state)
        assertEquals(mockAccount, state.account)
        assertEquals(mockMovements, state.movements)
    }

    @Test
    fun `loadAccountDetail with empty movements emits Empty`() = runTest {
        coEvery { productsRepository.getProductById("1") } returns mockAccount
        coEvery { getAccountMovementsUseCase("1") } returns Result.Success(emptyList())

        viewModel.loadAccountDetail("1")

        val state = viewModel.uiState.value
        assertIs<AccountDetailUiState.Empty>(state)
        assertEquals(mockAccount, state.account)
    }

    @Test
    fun `loadAccountDetail with null account emits Error`() = runTest {
        coEvery { productsRepository.getProductById("unknown") } returns null

        viewModel.loadAccountDetail("unknown")

        val state = viewModel.uiState.value
        assertIs<AccountDetailUiState.Error>(state)
        assertEquals("Cuenta no encontrada", state.message)
    }

    @Test
    fun `loadAccountDetail with movements fetch error emits Error`() = runTest {
        val errorMessage = "Error de red"
        coEvery { productsRepository.getProductById("1") } returns mockAccount
        coEvery { getAccountMovementsUseCase("1") } returns Result.Error(Exception(errorMessage))

        viewModel.loadAccountDetail("1")

        val state = viewModel.uiState.value
        assertIs<AccountDetailUiState.Error>(state)
        assertEquals(errorMessage, state.message)
    }

    @Test
    fun `loadAccountDetail resets to Loading on each call`() = runTest {
        coEvery { productsRepository.getProductById("1") } returns mockAccount
        coEvery { getAccountMovementsUseCase("1") } returns Result.Success(mockMovements)

        viewModel.loadAccountDetail("1")
        assertIs<AccountDetailUiState.Success>(viewModel.uiState.value)

        coEvery { productsRepository.getProductById("1") } returns null
        viewModel.loadAccountDetail("1")
        assertIs<AccountDetailUiState.Error>(viewModel.uiState.value)
    }
}
