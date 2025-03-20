package com.example.sightreadingapp.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(userName: String, onStartClicked: () -> Unit, onNoteQuizClicked: () -> Unit) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome back, $userName", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onStartClicked) {
                Text("quiz")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNoteQuizClicked){
                Text("Note Quiz")
            }
        }
    }
}