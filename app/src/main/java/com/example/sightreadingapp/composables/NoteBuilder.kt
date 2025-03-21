import android.content.ClipData.Item
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sightreadingapp.data.Accidentals
import com.example.sightreadingapp.data.NoteOptions
import com.example.sightreadingapp.data.NoteResourcesAndAnswer
import com.example.sightreadingapp.data.Question
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteBuilder(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit
){

    val availableQuestions = mutableListOf(*NoteResourcesAndAnswer.entries.toTypedArray()) // gets List of all ResourcesAndAnswers of this type
    val possibleNotes = listOf(*NoteOptions.entries.toTypedArray()).filterNot { it == NoteOptions.NONESELECTED } // gets a list of all NoteOptions
    val possibleAccidents = listOf(*Accidentals.entries.toTypedArray()) // gets all types of accidentals
    var incrementingId: Int = 1

    // Generate random question function
    fun chooseRandomQuestion(): Question? {
        if (availableQuestions.isEmpty()) {
            return null
        }
        val randomQuestion = availableQuestions.random()
        availableQuestions.remove(randomQuestion)
        val correctNote = randomQuestion.correctNote.note
        val correctAccidental = randomQuestion.correctAccidental.accident
        val options = mutableListOf<Pair<String, String>>()
        incrementingId++

        return Question(
            id = incrementingId,
            noteResource = randomQuestion.drawableResource,
            options = options.map { "${it.first}${it.second}" }, // redundant code  as there is like no options
            correctAnswer = "$correctNote$correctAccidental"
        )


    }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var hasAttempted by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var userAccident by remember { mutableStateOf<Accidentals?>(null) }
    var userNote by remember { mutableStateOf<NoteOptions?>(null) }
    var userAccidentString by remember { mutableStateOf<String>("") }
    var userNoteString by remember { mutableStateOf<String>("") }
    var userAnswerString by remember { mutableStateOf(mutableSetOf<Pair<String?, String?>>(Pair("", ""))) }

    // Create a list of questions dynamically
    val questions = remember { mutableStateListOf<Question>() }

    // Flag to indicate whether the questions are ready
    var isQuestionsReady by remember { mutableStateOf(false) }

    // Generate the questions when the screen first loads
    LaunchedEffect(Unit) {
        repeat(10) {
            chooseRandomQuestion()?.let { question ->
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
            Text(
                userAnswerString.toString().trim('{', '[', ']', '}', ',', '(', ')').replace(",", ""),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            if (resultMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(resultMessage)
                userAccidentString = ""
                userNoteString = ""
            }
            // Display the current question's note
            Image(
                painter = painterResource(id = currentQuestion.noteResource),
                contentDescription = "Treble Clef Note",
                modifier = Modifier.size(500.dp) // increased this so the image wont be super small
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                possibleAccidents.forEach { accidentals ->
                    Button( onClick = {
                        userAccident = accidentals
                        userAccidentString = userAccident!!.accident
                    }) {
                        Text(accidentals.toString())

                        if (userAccident != null) {
                            userAnswerString = mutableSetOf(userNoteString to userAccidentString)
                        }
                    }
                }
            }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    possibleNotes.forEach { note ->
                        Button(
                            onClick = {
                                userNote = note
                                userNoteString = userNote!!.note
                                userAnswerString = mutableSetOf(userNoteString to userAccidentString)
                            },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(note.toString())
                        }
                    }
                }
                Row {
                    Button(onClick = {
                        if (!hasAttempted) {
                            hasAttempted = true
                            val submittedAnswer = userAnswerString.map { "${it.first}${it.second}" }.joinToString("")

                            if (submittedAnswer == currentQuestion.correctAnswer) {
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

                    }) {
                        Text("Submit Answer")
                    }
                }
            }
        }
    }
}
