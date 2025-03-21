package com.example.sightreadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Data Model for a Quiz Question ---
data class Question(
    val id: Int,
    val noteResource: Int,  // Drawable resource for the Treble Clef note.
    val options: List<String>,
    val correctAnswer: String
)

enum class NoteOptions(val note: String) {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G");
}

enum class Accidentals(val accident: String){
    NONE(""),
    FLAT("♭"),
    SHARP("♯")
}

// chose an enum for both of these to enforce consistency
enum class NoteResourcesAndAnswer(val correctNote: NoteOptions, val correctAccidental: Accidentals, val drawableResource: Int) {
    TREBLE_NOTE_A_LEDGER(NoteOptions.A, Accidentals.NONE, R.drawable.treble_note_a_ledger),
    TREBLE_NOTE_A_SPACE(NoteOptions.A, Accidentals.NONE, R.drawable.treble_note_a_space),
    TREBLE_NOTE_B_LINE(NoteOptions.B, Accidentals.NONE, R.drawable.treble_note_b_line),
    TREBLE_NOTE_C_SPACE(NoteOptions.C, Accidentals.NONE, R.drawable.treble_note_c_space),
    TREBLE_NOTE_D_BELOW(NoteOptions.D, Accidentals.NONE, R.drawable.treble_note_d_below),
    TREBLE_NOTE_D_LINE(NoteOptions.D, Accidentals.NONE, R.drawable.treble_note_d_line),
    TREBLE_NOTE_E_BOTTOM(NoteOptions.E, Accidentals.NONE, R.drawable.treble_note_e_bottom),
    TREBLE_NOTE_E_TOP_SPACE(NoteOptions.E, Accidentals.NONE, R.drawable.treble_note_e_top_space),
    TREBLE_NOTE_F_SPACE(NoteOptions.F, Accidentals.NONE, R.drawable.treble_note_f_space),
    TREBLE_NOTE_F_TOP_LINE(NoteOptions.F, Accidentals.NONE, R.drawable.treble_note_f_top_line),
    TREBLE_NOTE_G_ABOVE(NoteOptions.G, Accidentals.NONE, R.drawable.treble_note_g_above),
    TREBLE_NOTE_G_LINE(NoteOptions.G, Accidentals.NONE, R.drawable.treble_note_g_line),

    TREBLE_NOTE_A_LEDGER_FLAT(NoteOptions.A, Accidentals.FLAT, R.drawable.treble_note_a_ledger_flat),
    TREBLE_NOTE_A_SPACE_FLAT(NoteOptions.A, Accidentals.FLAT, R.drawable.treble_note_a_space_flat),
    TREBLE_NOTE_B_LINE_FLAT(NoteOptions.B, Accidentals.FLAT, R.drawable.treble_note_b_line_flat),
    TREBLE_NOTE_C_SPACE_FLAT(NoteOptions.C, Accidentals.FLAT, R.drawable.treble_note_c_space_flat),
    TREBLE_NOTE_D_BELOW_FLAT(NoteOptions.D, Accidentals.FLAT, R.drawable.treble_note_d_below_flat),
    TREBLE_NOTE_D_LINE_FLAT(NoteOptions.D, Accidentals.FLAT, R.drawable.treble_note_d_line_flat),
    TREBLE_NOTE_E_BOTTOM_FLAT(NoteOptions.E, Accidentals.FLAT, R.drawable.treble_note_e_bottom_flat),
    TREBLE_NOTE_E_TOP_SPACE_FLAT(NoteOptions.E, Accidentals.FLAT, R.drawable.treble_note_e_top_space_flat),
    TREBLE_NOTE_F_SPACE_FLAT(NoteOptions.F, Accidentals.FLAT, R.drawable.treble_note_f_space_flat),
    TREBLE_NOTE_F_TOP_LINE_FLAT(NoteOptions.F, Accidentals.FLAT, R.drawable.treble_note_f_top_line_flat),
    TREBLE_NOTE_G_ABOVE_FLAT(NoteOptions.G, Accidentals.FLAT, R.drawable.treble_note_g_above_flat),
    TREBLE_NOTE_G_LINE_FLAT(NoteOptions.G, Accidentals.FLAT, R.drawable.treble_note_g_line_flat),

    TREBLE_NOTE_A_LEDGER_SHARP(NoteOptions.A, Accidentals.SHARP, R.drawable.treble_note_a_ledger_sharp),
    TREBLE_NOTE_A_SPACE_SHARP(NoteOptions.A, Accidentals.SHARP, R.drawable.treble_note_a_space_sharp),
    TREBLE_NOTE_B_LINE_SHARP(NoteOptions.B, Accidentals.SHARP, R.drawable.treble_note_b_line_sharp),
    TREBLE_NOTE_C_SPACE_SHARP(NoteOptions.C, Accidentals.SHARP, R.drawable.treble_note_c_space_sharp),
    TREBLE_NOTE_D_BELOW_SHARP(NoteOptions.D, Accidentals.SHARP, R.drawable.treble_note_d_below_sharp),
    TREBLE_NOTE_D_LINE_SHARP(NoteOptions.D, Accidentals.SHARP, R.drawable.treble_note_d_line_sharp),
    TREBLE_NOTE_E_BOTTOM_SHARP(NoteOptions.E, Accidentals.SHARP, R.drawable.treble_note_e_bottom_sharp),
    TREBLE_NOTE_E_TOP_SPACE_SHARP(NoteOptions.E, Accidentals.SHARP, R.drawable.treble_note_e_top_space_sharp),
    TREBLE_NOTE_F_SPACE_SHARP(NoteOptions.F, Accidentals.SHARP, R.drawable.treble_note_f_space_sharp),
    TREBLE_NOTE_F_TOP_LINE_SHARP(NoteOptions.F, Accidentals.SHARP, R.drawable.treble_note_f_top_line_sharp),
    TREBLE_NOTE_G_ABOVE_SHARP(NoteOptions.G, Accidentals.SHARP, R.drawable.treble_note_g_above_sharp),
    TREBLE_NOTE_G_LINE_SHARP(NoteOptions.G, Accidentals.SHARP, R.drawable.treble_note_g_line_sharp),
}

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
                var userProfile by remember {
                    mutableStateOf(ProfileRepository.getProfileById(context, currentProfileId))
                }

                // Navigation graph: Welcome, Quiz, and Leaderboard screens.
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") {
                        WelcomeScreen(
                            userProfile?.name ?: "User",
                            onStartClicked = { navController.navigate("quiz") },
                            onNoteQuizClicked = { navController.navigate("noteBuilder") }
                        )
                    }
                    composable("noteBuilder") {
                        noteBuilder(
                            onQuizFinished = {
                                // Refresh the profile after the quiz.
                                userProfile = ProfileRepository.getProfileById(context, currentProfileId)
                                navController.navigate("leaderboard")
                            },
                            updateScore = { points ->
                                val updatedScore = (userProfile?.score ?: 0) + points
                                userProfile = userProfile?.copy(score = updatedScore)
                                userProfile?.let { ProfileRepository.updateProfileScore(context, it.id, updatedScore) }
                            }
                        )
                    }
                    composable("quiz") {
                        QuizScreen(
                            onQuizFinished = {
                                // Refresh the profile after the quiz.
                                userProfile = ProfileRepository.getProfileById(context, currentProfileId)
                                navController.navigate("leaderboard")
                            },
                            updateScore = { points ->
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
fun WelcomeScreen(
    userName: String,
    onStartClicked: () -> Unit,
    onNoteQuizClicked: () -> Unit
) {
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
            Button(onClick = onNoteQuizClicked) {
                Text("Note Quiz")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun noteBuilder(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit
) {
    // TODO: Build out your noteBuilder content
}

// -----------------------------------
// Quiz Screen with Progress Bar
// -----------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit
) {
    // 1. Define how many questions to generate
    val totalQuestions = 10

    val availableQuestions = mutableListOf(*NoteResourcesAndAnswer.entries.toTypedArray())
    val possibleNotes = listOf(*NoteOptions.entries.toTypedArray())
    val possibleAccidents = listOf(*Accidentals.entries.toTypedArray())
    var incrementingId: Int = 1

    fun generateRandomQuestion(): Question? {
        if (availableQuestions.isEmpty()) return null

        val randomQuestion = availableQuestions.random()
        availableQuestions.remove(randomQuestion)

        val correctNote = randomQuestion.correctNote.note
        val correctAccidental = randomQuestion.correctAccidental.accident

        val shuffledNotes = possibleNotes.filter { it.note != correctNote }.take(3)
        val options = mutableListOf<Pair<String, String>>().apply {
            add(correctNote to correctAccidental)
        }

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

    // 2. Track current question, user attempts, etc.
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var hasAttempted by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var feedbackAnimationRes by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val questions = remember { mutableStateListOf<Question>() }
    var isQuestionsReady by remember { mutableStateOf(false) }

    // 3. Generate questions on first load
    LaunchedEffect(Unit) {
        repeat(totalQuestions) {
            generateRandomQuestion()?.let { question -> questions.add(question) }
        }
        isQuestionsReady = true
    }

    if (!isQuestionsReady) {
        return // Wait until questions are ready
    }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    if (currentQuestion == null) {
        // No more questions, finish quiz
        feedbackAnimationRes = null
        onQuizFinished()
        return
    }

    // 4. Show a top bar with a progress bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quiz")
                        // Duolingo-like progress bar showing “Question X of Y”
                        QuizProgressBar(
                            currentQuestionIndex = currentQuestionIndex,
                            totalQuestions = totalQuestions
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // If we do NOT have an active animation, show the quiz UI
            if (feedbackAnimationRes == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Display the question’s note
                    Image(
                        painter = painterResource(id = currentQuestion.noteResource),
                        contentDescription = "Treble Clef Note",
                        modifier = Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    val context = LocalContext.current

                    // Answer options
                    currentQuestion.options.forEach { option ->
                        Button(
                            onClick = {
                                if (!hasAttempted) {
                                    hasAttempted = true
                                    if (option == currentQuestion.correctAnswer) {
                                        resultMessage = "Correct!"
                                        updateScore(10)
                                        playSound(context, R.raw.right_answer)
                                        feedbackAnimationRes = R.raw.smile_correct
                                    } else {
                                        resultMessage = "Wrong! Correct answer: ${currentQuestion.correctAnswer}"
                                        playSound(context, R.raw.incorrect_answer)
                                        feedbackAnimationRes = R.raw.smile_incorrect
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
                }
            } else {
                // If we DO have an active animation, show the full-screen overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Set a fixed size for the animation
                        AnswerFeedbackAnimation(
                            animationRes = feedbackAnimationRes!!,
                            durationMillis = if (resultMessage.contains("Correct")) 2000L else 3000L,
                            onAnimationEnd = {
                                feedbackAnimationRes = null
                                coroutineScope.launch {
                                    delay(150L)
                                    currentQuestionIndex++
                                    hasAttempted = false
                                    resultMessage = ""
                                }
                            },
                            modifier = Modifier.size(250.dp)  // Use a fixed size instead of fillMaxSize()
                        )
                        // Display the result text below the animation
                        if (resultMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = resultMessage,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------
// Simple Duolingo-like Progress Bar
// -----------------------------------
@Composable
fun QuizProgressBar(
    currentQuestionIndex: Int,
    totalQuestions: Int
) {
    // Convert current question index to 1-based for user display
    val currentNumber = currentQuestionIndex + 1

    // Calculate progress as a fraction from 0.0 to 1.0
    val progress = currentNumber.coerceAtMost(totalQuestions).toFloat() / totalQuestions

    Column(modifier = Modifier.fillMaxWidth()) {
        // Optional: “Question X of Y”
        Text(
            text = "Question $currentNumber of $totalQuestions",
            style = MaterialTheme.typography.bodyMedium
        )
        // Linear progress bar
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
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
