package com.example.sightreadingapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sightreadingapp.ui.viewmodel.NoteBuilderViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteBuilderScreen(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit,
    viewModel: NoteBuilderViewModel = viewModel()
) {
    // val coroutineScope = rememberCoroutineScope()

    if (!viewModel.isQuestionsReady) {
        // Optionally show a loading indicator.
        return
    }

    // Get the current question.
    val currentQuestion = viewModel.questions.getOrNull(viewModel.currentQuestionIndex)

    // When there are no more questions, trigger onQuizFinished once.
    if (currentQuestion == null) {
        LaunchedEffect(Unit) {
            onQuizFinished()
        }
        return
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Note Builder Quiz") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show the user's current answer.
            Text(
                text = viewModel.userAnswer,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            if (viewModel.resultMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = viewModel.resultMessage)
            }
            // Display the current question's note image.
            Image(
                painter = painterResource(id = currentQuestion.noteResource),
                contentDescription = "Note Image",
                modifier = Modifier.size(500.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    viewModel.possibleAccidents.forEach { accident ->
                        Button(
                            onClick = { viewModel.selectAccident(accident) },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(accident.toString())
                        }
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    viewModel.possibleNotes.forEach { note ->
                        Button(
                            onClick = { viewModel.selectNote(note) },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(note.toString())
                        }
                    }
                }
                Row {
                    Button(onClick = {
                        viewModel.submitAnswer(
                            onNextQuestion = { /* No extra action needed here */ },
                            updateScore = updateScore
                        )
                    }) {
                        Text("Submit Answer")
                    }
                }
            }
        }
    }
}
