package com.microsol.bancamovil.presentation.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.microsol.bancamovil.presentation.navigation.Screen
import com.microsol.bancamovil.presentation.screens.account_detail.AccountDetailScreen
import com.microsol.bancamovil.presentation.screens.products.ProductsScreen
import com.microsol.bancamovil.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    
    // Manejar navegación a login cuando la sesión expira o no es válida
    LaunchedEffect(uiState.shouldNavigateToLogin) {
        if (uiState.shouldNavigateToLogin) {
            onNavigateToLogin()
            viewModel.onNavigatedToLogin()
        }
    }
    
    // Mostrar diálogo de sesión expirada
    if (uiState.showSessionExpiredDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSessionExpiredDialog() },
            title = { Text("Sesión Expirada") },
            text = { Text("Tu sesión ha expirado por inactividad. Por favor, inicia sesión nuevamente.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.dismissSessionExpiredDialog() }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
    
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Products.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Products.route) {
                ProductsScreen(
                    onAccountClick = { account ->
                        navController.navigate(Screen.AccountDetail.createRoute(account.id))
                    }
                )
            }

            composable(Screen.AccountDetail.route) { backStackEntry ->
                val accountId = backStackEntry.arguments?.getString("accountId") ?: ""
                AccountDetailScreen(
                    accountId = accountId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
