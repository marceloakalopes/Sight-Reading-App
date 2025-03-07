package com.example.sightreadingapp.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String?=null,        // Generated automatically by Supabase
    val parentid: String,  // Parent's Supabase user id
    val name: String,
    val score: Int = 0
)
