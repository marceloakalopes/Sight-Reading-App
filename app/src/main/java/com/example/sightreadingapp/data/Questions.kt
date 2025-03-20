package com.example.sightreadingapp.data

data class Question(
    val id: Int,
    val noteResource: Int,  // Drawable resource for the Treble Clef note.
    val options: List<String>,
    val correctAnswer: String

)