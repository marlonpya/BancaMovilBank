package com.microsol.bancamovil.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.microsol.bancamovil.R
import com.microsol.bancamovil.presentation.components.BankingButton
import com.microsol.bancamovil.presentation.components.BankingTextField
import com.microsol.bancamovil.presentation.viewmodel.LoginAuthState
import com.microsol.bancamovil.presentation.viewmodel.LoginScreenViewModel
import com.microsol.bancamovil.presentation.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginScreenViewModel = hiltViewModel<LoginViewModel>()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoading = authState is LoginAuthState.Loading

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is LoginAuthState.Success -> {
                onLoginSuccess()
                viewModel.resetSuccessState()
            }
            is LoginAuthState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearError()
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    snackbarHostState,
                    modifier = Modifier.testTag("login_error_message")
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                LoginLogo()

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Banca Móvil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Interbank",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(48.dp))

                BankingTextField(
                    value = formState.username,
                    onValueChange = viewModel::updateUsername,
                    label = "Usuario",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.height(16.dp))

                BankingTextField(
                    value = formState.password,
                    onValueChange = viewModel::updatePassword,
                    label = "Contraseña",
                    isPassword = true,
                    keyboardType = KeyboardType.Password
                )

                Spacer(modifier = Modifier.height(32.dp))

                BankingButton(
                    text = "INGRESAR",
                    onClick = viewModel::login,
                    isLoading = isLoading,
                    modifier = Modifier.testTag("login_button")
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Usuarios de prueba:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = """
                                • userTest1 / passTest1 (Login exitoso)
                                • User@test / TestPass_ (Login exitoso)
                                • user123& / 123456 (Login exitoso)
                                • Cualquier otro usuario (Login Error)
                                """.trimIndent(),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("login_loader")
                    .pointerInput(Unit) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {})
}


@Preview
@Composable
private fun LoginLogo() {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = "Logo Interbank",
        modifier = Modifier.size(120.dp),
        contentScale = ContentScale.Fit
    )
}