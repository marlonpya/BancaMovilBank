package com.microsol.bancamovil.presentation.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.presentation.components.MaterialIcon
import com.microsol.bancamovil.presentation.navigation.Screen
import com.microsol.bancamovil.presentation.screens.account_detail.AccountDetailScreen
import com.microsol.bancamovil.presentation.screens.bottomNavItems
import com.microsol.bancamovil.presentation.screens.operations.OperationsScreen
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
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            MaterialIcon(
                                iconName = item.icon,
                                size = 24.dp
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Products.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Products.route) {
                ProductsScreen(
                    onAccountClick = { account ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("selected_account", account)
                        navController.navigate(Screen.AccountDetail.createRoute(account.id))
                    }
                )
            }
            
            composable(Screen.Operations.route) {
                OperationsScreen()
            }
            
            composable(Screen.AccountDetail.route) { backStackEntry ->
                val accountId = backStackEntry.arguments?.getString("accountId") ?: ""
                val previousEntry = remember(backStackEntry) {
                    navController.previousBackStackEntry
                }
                val selectedAccount = previousEntry?.savedStateHandle?.get<BankAccount>("selected_account")
                
                AccountDetailScreen(
                    accountId = accountId,
                    account = selectedAccount,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
