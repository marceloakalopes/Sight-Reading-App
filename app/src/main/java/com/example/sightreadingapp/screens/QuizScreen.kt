package com.example.sightreadingapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.sightreadingapp.R
import com.example.sightreadingapp.models.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit
) {
    var hasAttempted by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val question = Question(
        id = 1,
        noteResource = R.drawable.treble_note_e_bottom,
        options = listOf("E", "F", "G", "A"),
        correctAnswer = "E"
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quiz") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = question.noteResource),
                contentDescription = "Treble Clef Note",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            question.options.forEach { option ->
                Button(
                    onClick = {
                        if (!hasAttempted) {
                            hasAttempted = true
                            if (option == question.correctAnswer) {
                                resultMessage = "Correct!"
                                updateScore(10)
                                coroutineScope.launch {
                                    delay(1000L)
                                    onQuizFinished()
                                }
                            } else {
                                resultMessage = "Wrong! Correct: ${question.correctAnswer}"
                                coroutineScope.launch {
                                    delay(2000L)
                                    onQuizFinished()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(option)
                }
            }
            if (resultMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(resultMessage)
            }
        }
    }
}
