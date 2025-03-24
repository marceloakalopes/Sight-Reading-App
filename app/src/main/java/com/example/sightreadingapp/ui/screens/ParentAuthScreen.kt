package com.example.sightreadingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sightreadingapp.ui.viewmodel.ParentAuthViewModel
import com.example.sightreadingapp.ui.viewmodel.ParentAuthViewModelFactory
import io.github.jan.supabase.auth.user.UserInfo

@Composable
fun ParentAuthScreen(
    onAuthSuccess: (email: String, userId: String) -> Unit
) {
    // Get the current context
    val context = LocalContext.current
    // Create a ViewModel factory
    val factory = ParentAuthViewModelFactory(context)
    // Create an instance of the ViewModel
    val viewModel: ParentAuthViewModel = viewModel(factory = factory)
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // When a user is available, trigger the onAuthSuccess callback
    LaunchedEffect(uiState.user) {
        uiState.user?.let { user: UserInfo ->
            onAuthSuccess(user.email ?: "", user.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            label = { Text("Parent Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChanged,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.signUp() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Sign Up")
        }
        if (uiState.errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
