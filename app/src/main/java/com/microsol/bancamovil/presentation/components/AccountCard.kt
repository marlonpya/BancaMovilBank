package com.microsol.bancamovil.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.microsol.bancamovil.domain.model.AccountType
import com.microsol.bancamovil.domain.model.BankAccount
import com.microsol.bancamovil.domain.model.Currency
import java.text.DecimalFormat

@Composable
fun AccountCard(
    account: BankAccount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de cuenta
            MaterialIcon(
                iconName = when (account.accountType) {
                    AccountType.SAVINGS -> "savings"
                    AccountType.USD_SAVINGS -> "attach_money"
                },
                size = 36.dp,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))
            
            // Información de la cuenta
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getAccountTypeName(account.accountType),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${account.currency.symbol} ${decimalFormat.format(account.balance)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (account.balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun getAccountTypeName(accountType: AccountType): String = when (accountType) {
    AccountType.SAVINGS -> "Cuenta soles"
    AccountType.USD_SAVINGS -> "Cuenta dólares"
}

@Preview
@Composable
fun AccountCardPreview() {
    val account = BankAccount(
        id = "123",
        accountNumber = "001-123456-0-01",
        accountType = AccountType.SAVINGS,
        balance = 1500.75,
        currency = Currency.SOLES,
        isActive = true
    )
    AccountCard(
        account = account,
        onClick = {}
    )
}


