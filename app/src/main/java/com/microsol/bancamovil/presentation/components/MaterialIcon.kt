package com.microsol.bancamovil.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MaterialIcon(
    iconName: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    val icon = getIconByName(iconName)

    Icon(
        imageVector = icon,
        contentDescription = iconName,
        modifier = modifier.size(size),
        tint = tint
    )
}

private fun getIconByName(iconName: String): ImageVector {
    return when (iconName) {
        //"account_balance" -> Icons.Filled.AccountBalanceWallet
        //"attach_money" -> Icons.Filled.AccountBalanceWallet
        //"account_balance_wallet" -> Icons.Filled.AccountBalanceWallet
        //"credit_card" -> Icons.Filled.AccountBalanceWallet
        //"savings" -> Icons.Filled.AccountBalanceWallet
        //"swap_horiz" -> Icons.Filled.Swap
        "arrow_back" -> Icons.Filled.ArrowBack
        //"receipt_long" -> Icons.Filled.Receipt
        "home" -> Icons.Filled.Home
        else -> Icons.Filled.Info
    }
}

