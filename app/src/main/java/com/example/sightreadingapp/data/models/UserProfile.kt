package com.example.sightreadingapp.data.models

import kotlinx.serialization.Serializable

/**
 * Represents a user profile.
 *
 * @param id The unique identifier for the user profile.
 * @param parentid The Supabase user id of the parent.
 * @param name The name of the user.
 * @param score The user's score.
 */
@Serializable
data class UserProfile(
    val id: String?=null,        // Generated automatically by Supabase
    val parentid: String,  // Parent's Supabase user id
    val name: String,
    val score: Int = 0
)
