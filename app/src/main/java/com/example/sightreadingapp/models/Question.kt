package com.example.sightreadingapp.models

data class Question(
    val id: Int,
    val noteResource: Int,  // Drawable resource id
    val options: List<String>,
    val correctAnswer: String
)
