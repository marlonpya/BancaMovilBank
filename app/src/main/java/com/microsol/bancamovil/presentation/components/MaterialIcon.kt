package com.microsol.bancamovil.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.SyncProblem
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
    Icon(
        imageVector = iconByName(iconName),
        contentDescription = iconName,
        modifier = modifier,
        tint = tint
    )
}

private fun iconByName(name: String): ImageVector = when (name) {
    "savings"                -> Icons.Outlined.Savings
    "account_balance_wallet" -> Icons.Outlined.AccountBalanceWallet
    "swap_horiz"             -> Icons.Outlined.SwapHoriz
    "receipt_long"           -> Icons.Outlined.ReceiptLong
    "attach_money"           -> Icons.Outlined.AttachMoney
    "arrow_back"             -> Icons.Outlined.ArrowBack
    "home"                   -> Icons.Outlined.Home
    "share"                  -> Icons.Outlined.Share
    "error"                  -> Icons.Outlined.Error
    "sync_problem"           -> Icons.Outlined.SyncProblem
    else                     -> Icons.Outlined.Info
}
