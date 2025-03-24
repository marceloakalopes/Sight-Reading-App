package com.example.sightreadingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    userName: String,
    onStartClicked: () -> Unit,
    onNoteBuilderClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome, $userName!", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onStartClicked) {
            Text("Start Standard Quiz")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNoteBuilderClicked) {
            Text("Start Note Builder Quiz")
        }
    }
}
