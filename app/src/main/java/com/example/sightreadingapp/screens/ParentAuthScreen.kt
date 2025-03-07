package com.example.sightreadingapp.screens

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
import com.example.sightreadingapp.session.SessionManager
import com.example.sightreadingapp.repository.SupabaseAuthRepository
import kotlinx.coroutines.launch

@Composable
fun ParentAuthScreen(
    onAuthSuccess: (email: String, userId: String) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authRepository = remember { SupabaseAuthRepository(sessionManager) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Parent Email") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val user = authRepository.login(email, password)
                        onAuthSuccess(user.email ?: "", user.id)
                    } catch (e: Exception) {
                        errorMessage = e.localizedMessage ?: "Login error"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Login") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val user = authRepository.signUp(email, password)
                        onAuthSuccess(user.email ?: "", user.id)
                    } catch (e: Exception) {
                        errorMessage = e.localizedMessage ?: "Sign Up error"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Sign Up") }
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
