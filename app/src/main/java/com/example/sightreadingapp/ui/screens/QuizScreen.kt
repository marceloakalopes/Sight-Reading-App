package com.example.sightreadingapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sightreadingapp.data.models.Question
import com.example.sightreadingapp.data.models.Accidentals
import com.example.sightreadingapp.data.models.NoteOptions
import com.example.sightreadingapp.data.models.NoteResourcesAndAnswer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit,
) {
    // Remember these so they persist across recompositions.
    val availableQuestions = remember { mutableListOf(*NoteResourcesAndAnswer.entries.toTypedArray()) }
    val possibleNotes = remember { listOf(*NoteOptions.entries.toTypedArray()) }
    val possibleAccidents = remember { listOf(*Accidentals.entries.toTypedArray()) }
    var incrementingId by remember { mutableIntStateOf(1) }

    // Generate random question function.
    fun generateRandomQuestion(): Question? {
        if (availableQuestions.isEmpty()) {
            return null
        }

        val randomQuestion = availableQuestions.random()
        availableQuestions.remove(randomQuestion)

        val correctNote = randomQuestion.correctNote.note
        val correctAccidental = randomQuestion.correctAccidental.accident

        val shuffledNotes = possibleNotes.filter { it.note != correctNote }.take(3)
        val options = mutableListOf<Pair<String, String>>()
        options.add(correctNote to correctAccidental)

        val incorrectOptions = mutableListOf<Pair<String, String>>()
        for (note in shuffledNotes) {
            for (accidental in possibleAccidents) {
                incorrectOptions.add(note.note to accidental.accident)
            }
        }
        incorrectOptions.shuffle()
        options.addAll(incorrectOptions.take(3))
        options.shuffle()

        incrementingId++

        return Question(
            id = incrementingId,
            noteResource = randomQuestion.drawableResource,
            options = options.map { "${it.first}${it.second}" },
            correctAnswer = "$correctNote$correctAccidental"
        )
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var hasAttempted by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Create a list of questions dynamically.
    val questions = remember { mutableStateListOf<Question>() }

    // Flag to indicate whether the questions are ready.
    var isQuestionsReady by remember { mutableStateOf(false) }

    // Generate the questions when the screen first loads.
    LaunchedEffect(Unit) {
        repeat(10) {
            generateRandomQuestion()?.let { question ->
                questions.add(question)
            }
        }
        isQuestionsReady = true
    }

    if (!isQuestionsReady) {
        return // Return early if questions aren't ready yet.
    }

    // Get the current question.
    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    // End the quiz when all questions have been answered.
    if (currentQuestion == null) {
        LaunchedEffect(Unit) {
            onQuizFinished()
        }
        return
    }

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
            // Display the current question's note.
            Image(
                painter = painterResource(id = currentQuestion.noteResource),
                contentDescription = "Treble Clef Note",
                modifier = Modifier.size(500.dp)
            )
            // Display answer options.
            currentQuestion.options.forEach { option ->
                Button(
                    onClick = {
                        if (!hasAttempted) {
                            hasAttempted = true
                            if (option == currentQuestion.correctAnswer) {
                                resultMessage = "Correct!"
                                updateScore(10)
                                coroutineScope.launch {
                                    delay(1000L)
                                    currentQuestionIndex++
                                    hasAttempted = false
                                    resultMessage = ""
                                }
                            } else {
                                resultMessage = "Wrong! Correct answer: ${currentQuestion.correctAnswer}"
                                coroutineScope.launch {
                                    delay(2000L)
                                    currentQuestionIndex++
                                    hasAttempted = false
                                    resultMessage = ""
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
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
