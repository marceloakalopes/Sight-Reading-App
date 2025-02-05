package com.example.sightreadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data class for a quiz question.
data class Question(
    val id: Int,
    val noteResource: Int,  // Drawable resource for the Treble Clef note image.
    val options: List<String>,
    val correctAnswer: String
)

// Data class for a leaderboard entry.
data class LeaderboardEntry(
    val name: String,
    val score: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Wrap our app in MaterialTheme (using Material3).
            MaterialTheme {
                // Create a NavController for navigation between screens.
                val navController = rememberNavController()
                // In-memory state to track the user's score.
                var userScore by remember { mutableIntStateOf(0) }

                // Define our navigation graph with three routes.
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") {
                        WelcomeScreen(onStartClicked = { navController.navigate("quiz") })
                    }
                    composable("quiz") {
                        QuizScreen(
                            onQuizFinished = { navController.navigate("leaderboard") },
                            updateScore = { points -> userScore += points }
                        )
                    }
                    composable("leaderboard") {
                        LeaderboardScreen(
                            userScore = userScore,
                            onRestartClicked = {
                                // Reset the score and navigate back to Welcome.
                                userScore = 0
                                navController.navigate("welcome") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStartClicked: () -> Unit) {
    // A simple welcome screen with a greeting and a "Start" button.
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome back, User", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onStartClicked) {
                Text("Start")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit
) {
    // Create 10 quiz questions using Treble Clef note positions.
    val questions = listOf(
        Question(
            id = 1,
            noteResource = R.drawable.treble_note_e_bottom,  // Bottom line (E)
            options = listOf("E", "F", "G", "A"),
            correctAnswer = "E"
        ),
        Question(
            id = 2,
            noteResource = R.drawable.treble_note_f_space,   // First space (F)
            options = listOf("F", "G", "A", "B"),
            correctAnswer = "F"
        ),
        Question(
            id = 3,
            noteResource = R.drawable.treble_note_g_line,    // Second line (G)
            options = listOf("G", "A", "B", "C"),
            correctAnswer = "G"
        ),
        Question(
            id = 4,
            noteResource = R.drawable.treble_note_a_space,   // Second space (A)
            options = listOf("A", "B", "C", "D"),
            correctAnswer = "A"
        ),
        Question(
            id = 5,
            noteResource = R.drawable.treble_note_b_line,    // Third line (B)
            options = listOf("B", "C", "D", "E"),
            correctAnswer = "B"
        ),
        Question(
            id = 6,
            noteResource = R.drawable.treble_note_c_space,   // Third space (C)
            options = listOf("C", "D", "E", "F"),
            correctAnswer = "C"
        ),
        Question(
            id = 7,
            noteResource = R.drawable.treble_note_d_line,    // Fourth line (D)
            options = listOf("D", "E", "F", "G"),
            correctAnswer = "D"
        ),
        Question(
            id = 8,
            noteResource = R.drawable.treble_note_e_top_space,  // Fourth space (E)
            options = listOf("E", "F", "G", "A"),
            correctAnswer = "E"
        ),
        Question(
            id = 9,
            noteResource = R.drawable.treble_note_f_top_line,  // Fifth line (F)
            options = listOf("F", "G", "A", "B"),
            correctAnswer = "F"
        ),
        Question(
            id = 10,
            noteResource = R.drawable.treble_note_a_ledger,    // Ledger note above the staff (A)
            options = listOf("A", "B", "C", "D"),
            correctAnswer = "A"
        )
    )

    // Track the current question index and whether the user has already attempted the current question.
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var hasAttempted by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // If all questions have been answered, navigate to the leaderboard.
    if (currentQuestionIndex >= questions.size) {
        LaunchedEffect(Unit) { onQuizFinished() }
        return
    }

    val currentQuestion = questions[currentQuestionIndex]

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quiz") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show the Treble Clef note image.
            Image(
                painter = painterResource(id = currentQuestion.noteResource),
                contentDescription = "Treble Clef Note",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Render each multiple-choice option as a button.
            currentQuestion.options.forEach { option ->
                Button(
                    onClick = {
                        // Only process the first attempt for each question.
                        if (!hasAttempted) {
                            hasAttempted = true
                            if (option == currentQuestion.correctAnswer) {
                                resultMessage = "Correct!"
                                updateScore(10)  // Award 10 points on first-try success.
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

            // Display the feedback message.
            if (resultMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(resultMessage)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    userScore: Int,
    onRestartClicked: () -> Unit
) {
    // Dummy leaderboard data.
    val dummyEntries = listOf(
        LeaderboardEntry("Alice", 50),
        LeaderboardEntry("Bob", 30),
        LeaderboardEntry("Charlie", 20),
        LeaderboardEntry("Dave", 10)
    )

    // Combine the dummy data with the current user's score.
    val allEntries = dummyEntries + LeaderboardEntry("You", userScore)
    // Sort in descending order by score.
    val sortedEntries = allEntries.sortedByDescending { it.score }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Leaderboard") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Leaderboard", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            sortedEntries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${index + 1}.", modifier = Modifier.width(30.dp))
                    Text(
                        text = entry.name,
                        modifier = Modifier.weight(1f),
                        color = if (entry.name == "You")
                            MaterialTheme.colorScheme.primary
                        else LocalContentColor.current
                    )
                    Text(entry.score.toString())
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRestartClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Restart")
            }
        }
    }
}
