package com.example.sightreadingapp.data.models

/**
 * Represents a single question in the quiz.
 *
 * @param id The unique identifier for the question.
 * @param noteResource The drawable resource id for the note image.
 * @param options The list of possible answers.
 * @param correctAnswer The correct answer.
 */
data class Question(
    val id: Int,
    val noteResource: Int,  // Drawable resource id
    val options: List<String>,
    val correctAnswer: String
)
