package com.microsol.bancamovil.presentation.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ShareAccountButton(
    account: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BankingButton(
        onClick = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, account)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Compartir cuenta bancaria")
            context.startActivity(shareIntent)
        },
        text = "Compartir",
        modifier = modifier
    )
}


@Preview
@Composable
fun ShareAccountButtonPreview() {
    ShareAccountButton(account = "123-456-789")
}
