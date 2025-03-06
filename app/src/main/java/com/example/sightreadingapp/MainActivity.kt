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
} // added accidentals to the scheme and now its 12 per type as we went lower


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
                        WelcomeScreen(userProfile?.name ?: "User",
                            onStartClicked = { navController.navigate("quiz") },
                            onNoteQuizClicked = { navController.navigate("noteBuilder") })
                    }
                    composable("noteBuilder"){
                        // just copied other probably could just make
                        // updateScore a function for quiz controllers
                        noteBuilder(
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun noteBuilder(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit
){}
// TODO make randomized quizzes
// kinda useful funky green eye attractor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit
) {
    val availableQuestions = mutableListOf(*NoteResourcesAndAnswer.entries.toTypedArray()) // gets List of all ResourcesAndAnswers of this type
    val possibleNotes = listOf(*NoteOptions.entries.toTypedArray()) // gets a list of all NoteOptions
    val possibleAccidents = listOf(*Accidentals.entries.toTypedArray()) // gets all types of accidentals
    var incrementingId: Int = 1

    // Generate random question function
    fun generateRandomQuestion(): Question? {
        if (availableQuestions.isEmpty()) {
            return null
        }

        val randomQuestion = availableQuestions.random() // Choose a random question

        availableQuestions.remove(randomQuestion) // Remove it from the available questions list

        // Extract the note and accidental from the random question
        val correctNote = randomQuestion.correctNote.note
        val correctAccidental = randomQuestion.correctAccidental.accident

        // Choose 3 possible notes that are not the correct answer (by note and accidental)
        val shuffledNotes = possibleNotes.filter { it.note != correctNote }.take(3)
        val shuffledAccidentals = possibleAccidents.filter { it.accident != correctAccidental }.take(3)

        // Create a list of options by pairing each note with each accidental, keeping it to just 4 combinations (including the correct one)
        val options = mutableListOf<Pair<String, String>>()

        // Add the correct note/accidental to the options list
        options.add(correctNote to correctAccidental)

        // Shuffle notes and accidentals separately, and pair them randomly, ensuring we only add 3 incorrect options
        val incorrectOptions = mutableListOf<Pair<String, String>>()
        for (note in shuffledNotes) {
            for (accidental in shuffledAccidentals) {
                incorrectOptions.add(note.note to accidental.accident)
            }
        }

        // Randomly shuffle and take the first 3 incorrect options
        incorrectOptions.shuffle()
        options.addAll(incorrectOptions.take(3))

        // Shuffle all the options to randomize their order
        options.shuffle()

        incrementingId++

        // Return the question with the randomized options
        return Question(
            id = incrementingId,
            noteResource = randomQuestion.drawableResource,
            options = options.map { "${it.first}${it.second}" }, // Combine note and accidental for options
            correctAnswer = "$correctNote$correctAccidental" // Combine note and accidental for the correct answer
        )
    }



    // State to manage current question and quiz state
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var hasAttempted by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Create a list of questions dynamically
    val questions = remember { mutableStateListOf<Question>() }

    // Flag to indicate whether the questions are ready
    var isQuestionsReady by remember { mutableStateOf(false) }

    // Generate the questions when the screen first loads
    LaunchedEffect(Unit) {
        repeat(10) {
            generateRandomQuestion()?.let { question ->
                questions.add(question)
            }
        }
        isQuestionsReady = true // sets the boolean to true when the questions are ready
    }

    if (!isQuestionsReady) {
        return // Returns early if questions aren't ready yet
    }

    // gets the current question
    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    // ends the quiz when everything is answered
    if (currentQuestion == null) {
        onQuizFinished()
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
            // Display the current question's note
            Image(
                painter = painterResource(id = currentQuestion.noteResource),
                contentDescription = "Treble Clef Note",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Display answer options
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
