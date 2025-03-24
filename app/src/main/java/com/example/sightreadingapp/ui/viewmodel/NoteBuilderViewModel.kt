package com.example.sightreadingapp.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sightreadingapp.data.models.Accidentals
import com.example.sightreadingapp.data.models.NoteResourcesAndAnswer
import com.example.sightreadingapp.data.models.Question
import com.example.sightreadingapp.data.models.NoteOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel for the Note Builder quiz.
 */
class NoteBuilderViewModel : ViewModel() {

    // Data sources from static model objects.
    private val availableQuestions = mutableListOf(*NoteResourcesAndAnswer.entries.toTypedArray())
    val possibleNotes: List<NoteOptions> = NoteOptions.entries.toList().filterNot { it == NoteOptions.NONESELECTED }
    val possibleAccidents: List<Accidentals> = Accidentals.entries.toList()

    // Mutable state variables.
    var incrementingId by mutableStateOf(1)
        private set

    private val _questions = mutableStateListOf<Question>()
    val questions: List<Question> get() = _questions

    var currentQuestionIndex by mutableStateOf(0)
        private set

    var resultMessage by mutableStateOf("")
        private set

    var hasAttempted by mutableStateOf(false)
        private set

    var userAccident by mutableStateOf<Accidentals?>(null)
        private set

    var userNote by mutableStateOf<NoteOptions?>(null)
        private set

    val userAnswer: String
        get() = "${userNote?.note ?: ""}${userAccident?.accident ?: ""}"

    var isQuestionsReady by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            repeat(10) {
                chooseRandomQuestion()?.let { question ->
                    _questions.add(question)
                }
            }
            isQuestionsReady = true
        }
    }

    private fun chooseRandomQuestion(): Question? {
        if (availableQuestions.isEmpty()) {
            return null
        }
        val randomQuestion = availableQuestions.random()
        availableQuestions.remove(randomQuestion)
        val correctNote = randomQuestion.correctNote.note
        val correctAccidental = randomQuestion.correctAccidental.accident
        incrementingId++
        return Question(
            id = incrementingId,
            noteResource = randomQuestion.drawableResource,
            options = emptyList(), // This quiz type does not use multiple options.
            correctAnswer = "$correctNote$correctAccidental"
        )
    }

    fun selectAccident(accident: Accidentals) {
        userAccident = accident
    }

    fun selectNote(note: NoteOptions) {
        userNote = note
    }

    /**
     * Submits the current answer. Depending on correctness, updates the result message,
     * calls updateScore, and then after a delay moves to the next question.
     */
    fun submitAnswer(onNextQuestion: () -> Unit, updateScore: (Int) -> Unit) {
        if (hasAttempted) return
        hasAttempted = true
        val currentQuestion = _questions.getOrNull(currentQuestionIndex) ?: return
        if (userAnswer == currentQuestion.correctAnswer) {
            resultMessage = "Correct!"
            updateScore(10)
            viewModelScope.launch {
                delay(1000L)
                currentQuestionIndex++
                resetForNextQuestion()
                onNextQuestion()
            }
        } else {
            resultMessage = "Wrong! Correct answer: ${currentQuestion.correctAnswer}"
            viewModelScope.launch {
                delay(2000L)
                currentQuestionIndex++
                resetForNextQuestion()
                onNextQuestion()
            }
        }
    }

    private fun resetForNextQuestion() {
        hasAttempted = false
        resultMessage = ""
        userAccident = null
        userNote = null
    }
}
