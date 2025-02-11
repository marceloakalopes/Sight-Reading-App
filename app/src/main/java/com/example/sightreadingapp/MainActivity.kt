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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Data Model for a Quiz Question ---
data class Question(
    val id: Int,
    val noteResource: Int,  // Drawable resource for the Treble Clef note.
    val options: List<String>,
    val correctAnswer: String
)

// --- Data Model for a Leaderboard Entry ---
data class LeaderboardEntry(val name: String, val score: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                // Retrieve the current profile from persistent storage.
                val currentProfileId = ProfileRepository.getCurrentProfileId(context)
                var userProfile by remember { mutableStateOf(ProfileRepository.getProfileById(context, currentProfileId)) }

                // Navigation graph: Welcome, Quiz, and Leaderboard screens.
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") {
                        WelcomeScreen(userProfile?.name ?: "User", onStartClicked = {
                            navController.navigate("quiz")
                        })
                    }
                    composable("quiz") {
                        QuizScreen(
                            onQuizFinished = {
                                // Refresh the profile after the quiz.
                                userProfile = ProfileRepository.getProfileById(context, currentProfileId)
                                navController.navigate("leaderboard")
                            },
                            updateScore = { points ->
                                // Update the profile's score both in memory and in persistent storage.
                                val updatedScore = (userProfile?.score ?: 0) + points
                                userProfile = userProfile?.copy(score = updatedScore)
                                userProfile?.let { ProfileRepository.updateProfileScore(context, it.id, updatedScore) }
                            }
                        )
                    }
                    composable("leaderboard") {
                        LeaderboardScreen(
                            userProfile = userProfile,
                            onRestartClicked = {
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
fun WelcomeScreen(userName: String, onStartClicked: () -> Unit) {
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
    // Define 10 quiz questions with Treble Clef note images.
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

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var hasAttempted by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    if (currentQuestionIndex >= questions.size) {
        LaunchedEffect(Unit) { onQuizFinished() }
        return
    }
    val currentQuestion = questions[currentQuestionIndex]

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
                painter = painterResource(id = currentQuestion.noteResource),
                contentDescription = "Treble Clef Note",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(userProfile: UserProfile?, onRestartClicked: () -> Unit) {
    // Dummy leaderboard entries.
    val dummyEntries = listOf(
        LeaderboardEntry("Alice", 50),
        LeaderboardEntry("Bob", 30),
        LeaderboardEntry("Charlie", 20),
        LeaderboardEntry("Dave", 10)
    )
    // Combine dummy entries with the current user's profile.
    val userEntry = userProfile?.let { LeaderboardEntry(it.name, it.score) }
    val allEntries = if (userEntry != null) dummyEntries + userEntry else dummyEntries
    val sortedEntries = allEntries.sortedByDescending { it.score }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Leaderboard") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Leaderboard", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            sortedEntries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}.", modifier = Modifier.width(30.dp))
                    Text(
                        entry.name,
                        modifier = Modifier.weight(1f),
                        color = if (userProfile != null && entry.name == userProfile.name)
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
