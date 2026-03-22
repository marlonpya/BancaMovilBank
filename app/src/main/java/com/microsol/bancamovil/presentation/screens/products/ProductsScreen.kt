package com.microsol.bancamovil.presentation.screens.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.presentation.components.AccountCard
import com.microsol.bancamovil.presentation.components.MaterialIcon
import com.microsol.bancamovil.presentation.viewmodel.ProductsContent
import com.microsol.bancamovil.presentation.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onAccountClick: (BankAccount) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
    val content = uiState.content

    // AlertDialog — error de carga inicial
    if (uiState.showLoadErrorDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLoadErrorDialog() },
            title = { Text("Error") },
            text = { Text("Ha ocurrido un error, vuelve a intentarlo.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissLoadErrorDialog()
                    viewModel.loadProducts()
                }) {
                    Text("Reintentar")
                }
            }
        )
    }

    // AlertDialog — error de pull-to-refresh
    if (uiState.showRefreshErrorDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRefreshErrorDialog() },
            title = { Text("Error") },
            text = { Text("Vuelve a intentarlo") },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissRefreshErrorDialog() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Productos",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = viewModel::refreshProducts,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (content) {
                is ProductsContent.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (content) {
                            is ProductsContent.LoadError -> {
                                item(key = "load_error") { LoadErrorItem() }
                            }
                            is ProductsContent.RefreshError -> {
                                item(key = "refresh_error") { RefreshErrorItem() }
                            }
                            is ProductsContent.Success -> {
                                if (content.products.isEmpty()) {
                                    item(key = "empty") { EmptyProductsItem() }
                                } else {
                                    items(content.products, key = { it.id }) { account ->
                                        AccountCard(
                                            account = account,
                                            onClick = { onAccountClick(account) }
                                        )
                                    }
                                }
                            }
                            else -> Unit
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadErrorItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MaterialIcon(
                iconName = "error",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "No se pudo obtener las cuentas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun RefreshErrorItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MaterialIcon(
                iconName = "sync_problem",
                size = 40.dp,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No se han podido cargar las cuentas, inténtelo de nuevo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyProductsItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MaterialIcon(
                iconName = "account_balance_wallet",
                size = 64.dp,
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tienes productos disponibles",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Desliza hacia abajo para actualizar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
