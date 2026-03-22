package com.microsol.bancamovil.presentation.screens

import com.microsol.bancamovil.presentation.navigation.Screen

data class BottomNavItem(
    val title: String,
    val icon: String,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Productos",
        icon = "account_balance_wallet",
        route = Screen.Products.route
    ),
    BottomNavItem(
        title = "Operaciones",
        icon = "swap_horiz",
        route = Screen.Operations.route
    )
)