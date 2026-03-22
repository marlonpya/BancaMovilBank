package com.microsol.bancamovil.presentation.viewmodel

import app.cash.turbine.test
import com.microsol.bancamovil.domain.model.AccountType
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.model.Currency
import com.microsol.bancamovil.domain.usecase.GetProductsUseCase
import com.microsol.bancamovil.domain.usecase.RefreshProductsUseCase
import com.microsol.bancamovil.domain.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @MockK
    private lateinit var getProductsUseCase: GetProductsUseCase

    @MockK
    private lateinit var refreshProductsUseCase: RefreshProductsUseCase

    private lateinit var viewModel: ProductsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockAccounts = listOf(
        BankAccount(
            id = "1",
            accountNumber = "001-123456-0-01",
            accountType = AccountType.SAVINGS,
            balance = 1500.0,
            currency = Currency.SOLES
        ),
        BankAccount(
            id = "2",
            accountNumber = "001-654321-0-01",
            accountType = AccountType.USD_SAVINGS,
            balance = 300.0,
            currency = Currency.DOLLARS
        )
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads products successfully`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(mockAccounts)

        viewModel = ProductsViewModel(getProductsUseCase, refreshProductsUseCase)

        val state = viewModel.uiState.value
        assertIs<ProductsContent.Success>(state.content)
        assertEquals(mockAccounts, (state.content as ProductsContent.Success).products)
        assertFalse(state.showLoadErrorDialog)
    }

    @Test
    fun `loadProducts error sets LoadError content and shows dialog`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Error(Exception("Network error"))

        viewModel = ProductsViewModel(getProductsUseCase, refreshProductsUseCase)

        val state = viewModel.uiState.value
        assertIs<ProductsContent.LoadError>(state.content)
        assertTrue(state.showLoadErrorDialog)
    }

    @Test
    fun `retry after error calls loadProducts again`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Error(Exception("Network error"))
        viewModel = ProductsViewModel(getProductsUseCase, refreshProductsUseCase)

        viewModel.dismissLoadErrorDialog()
        coEvery { getProductsUseCase() } returns Result.Success(mockAccounts)
        viewModel.loadProducts()

        val state = viewModel.uiState.value
        assertIs<ProductsContent.Success>(state.content)
        assertEquals(mockAccounts, (state.content as ProductsContent.Success).products)
        coVerify(exactly = 2) { getProductsUseCase() }
    }

    @Test
    fun `refreshProducts success updates content`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(mockAccounts)
        coEvery { refreshProductsUseCase() } returns Result.Success(mockAccounts.reversed())

        viewModel = ProductsViewModel(getProductsUseCase, refreshProductsUseCase)

        viewModel.uiState.test {
            awaitItem()
            viewModel.refreshProducts()
            val refreshing = awaitItem()
            assertTrue(refreshing.isRefreshing)
            val done = awaitItem()
            assertFalse(done.isRefreshing)
            assertIs<ProductsContent.Success>(done.content)
            assertEquals(mockAccounts.reversed(), (done.content as ProductsContent.Success).products)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshProducts error sets RefreshError content and shows dialog`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(mockAccounts)
        coEvery { refreshProductsUseCase() } returns Result.Error(Exception("Refresh failed"))

        viewModel = ProductsViewModel(getProductsUseCase, refreshProductsUseCase)

        viewModel.uiState.test {
            awaitItem()
            viewModel.refreshProducts()
            val refreshing = awaitItem()
            assertTrue(refreshing.isRefreshing)
            val done = awaitItem()
            assertFalse(done.isRefreshing)
            assertIs<ProductsContent.RefreshError>(done.content)
            assertTrue(done.showRefreshErrorDialog)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dismissLoadErrorDialog hides the dialog`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Error(Exception("Error"))
        viewModel = ProductsViewModel(getProductsUseCase, refreshProductsUseCase)

        assertTrue(viewModel.uiState.value.showLoadErrorDialog)
        viewModel.dismissLoadErrorDialog()
        assertFalse(viewModel.uiState.value.showLoadErrorDialog)
    }

    @Test
    fun `dismissRefreshErrorDialog hides the dialog`() = runTest {
        coEvery { getProductsUseCase() } returns Result.Success(mockAccounts)
        coEvery { refreshProductsUseCase() } returns Result.Error(Exception("Error"))
        viewModel = ProductsViewModel(getProductsUseCase, refreshProductsUseCase)

        viewModel.refreshProducts()
        assertTrue(viewModel.uiState.value.showRefreshErrorDialog)
        viewModel.dismissRefreshErrorDialog()
        assertFalse(viewModel.uiState.value.showRefreshErrorDialog)
    }
}
