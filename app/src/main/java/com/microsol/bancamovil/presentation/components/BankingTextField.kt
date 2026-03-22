package com.microsol.bancamovil.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun BankingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    supportingText: String? = null
) {

    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }

    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val filtered = newValue.text.removeEmojis()
            val newSelection = newValue.selection.end.coerceAtMost(filtered.length)
            textFieldValue = newValue.copy(
                text = filtered,
                selection = TextRange(newSelection)
            )
            onValueChange(filtered)
        },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        supportingText = if (supportingText != null) {
            { Text(supportingText) }
        } else null,
        singleLine = true
    )
}

fun String.removeEmojis(): String {
    val emojiRegex = Regex(
        "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF" +
                "\\u2600-\\u27FF\\u2300-\\u23FF" +
                "\\u2700-\\u27BF\\uFE00-\\uFE0F" +
                "\\u20D0-\\u20FF\\u2100-\\u21FF" +
                "\\u231A-\\u231B\\u23E9-\\u23F3" +
                "\\u25AA-\\u25FE\\u2614-\\u2615" +
                "\\u2648-\\u2653\\u26AA-\\u26BE]+"
    )
    return this.replace(emojiRegex, "")
}

@Preview
@Composable
fun BankingTextFieldPreview() {
    BankingTextField(
        value = "Sample Text",
        onValueChange = {},
        label = "Sample Label",
        isError = false,
        supportingText = "SupportingText"
    )
}
