package com.microsol.bancamovil.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.microsol.bancamovil.domain.model.Currency
import com.microsol.bancamovil.domain.model.Transaction
import com.microsol.bancamovil.domain.model.TransactionType
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("es", "PE")) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = dateFormat.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (transaction.amount >= 0) "+" else ""}${transaction.currency.symbol} ${decimalFormat.format(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = getTransactionColor(transaction.amount)
                )
            }
        }
    }
}

@Composable
private fun getTransactionColor(amount: Double): androidx.compose.ui.graphics.Color = when {
    amount > 0 -> MaterialTheme.colorScheme.primary
    amount < 0 -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurface
}

@Preview
@Composable
fun TransactionItemPreview() {
    val transaction = Transaction(
        id = "1",
        accountId = "12345",
        type = TransactionType.DEPOSIT,
        amount = 100.0,
        currency = Currency.SOLES,
        description = "Deposit in savings account",
        date = Date(),
        balance = 500.0,
    )
    TransactionItem(transaction = transaction)
}




