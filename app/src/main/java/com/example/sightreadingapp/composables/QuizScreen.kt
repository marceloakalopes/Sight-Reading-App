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
import com.example.sightreadingapp.data.Accidentals
import com.example.sightreadingapp.data.NoteOptions
import com.example.sightreadingapp.data.NoteResourcesAndAnswer
import com.example.sightreadingapp.data.Question
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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


        // Create a list of options by pairing each note with each accidental, keeping it to just 4 combinations (including the correct one)
        val options = mutableListOf<Pair<String, String>>()

        // Add the correct note/accidental to the options list
        options.add(correctNote to correctAccidental)


        val incorrectOptions = mutableListOf<Pair<String, String>>()
        for (note in shuffledNotes) {
            for (accidental in possibleAccidents) {
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
                modifier = Modifier.size(500.dp) // increased this so the image wont be super small
            )
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