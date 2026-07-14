package com.lifelink.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.LifeLinkPrimaryButton
import com.lifelink.core.ui.components.VerticalSpace

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onAuthSuccess()
    }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding).padding(24.dp)) {
            Text(
                if (isRegisterMode) "Create your account" else "Welcome back",
                style = MaterialTheme.typography.headlineMedium,
            )
            VerticalSpace(24)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            VerticalSpace(12)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
            )
            if (uiState is AuthUiState.Error) {
                VerticalSpace(8)
                Text((uiState as AuthUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            VerticalSpace(24)
            LifeLinkPrimaryButton(
                text = if (isRegisterMode) "Create account" else "Sign in",
                loading = uiState is AuthUiState.Loading,
                onClick = {
                    if (isRegisterMode) viewModel.register(email, password) else viewModel.signIn(email, password)
                },
            )
            TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                Text(if (isRegisterMode) "Already have an account? Sign in" else "New here? Create an account")
            }
        }
    }
}
