package com.example.sightreadingapp.repository

import com.example.sightreadingapp.models.UserProfile
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ProfileRepository {

    private val supabase = createSupabaseClient(
        supabaseUrl = "https://lhzquvsiudducczhvqrs.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxoenF1dnNpdWRkdWNjemh2cXJzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAxNzAwOTksImV4cCI6MjA1NTc0NjA5OX0.Jq-F1Am5EDS5eSd56MUeeiRmCD7NiUYhzgJw5TKhd1Q"
    ) {
        install(Postgrest)
    }

    suspend fun getProfilesForParent(parentId: String): List<UserProfile> = withContext(Dispatchers.IO) {
        supabase.postgrest["profiles"]
            .select() {
                filter {
                    eq("parentid", parentId)
                }
            }
            .decodeList<UserProfile>()
    }

    suspend fun createProfile(parentId: String, kidName: String): UserProfile = withContext(Dispatchers.IO) {
        val newProfile = UserProfile(
            parentid = parentId,
            name = kidName,
            score = 0
        )
        supabase.postgrest["profiles"]
            .insert(newProfile) {
                select()
            }
            .decodeSingle<UserProfile>()
    }

    suspend fun updateProfileScore(profileId: String, newScore: Int): Boolean = withContext(Dispatchers.IO) {
        supabase.postgrest["profiles"]
            .update({
                set("score", newScore)
            }) {
                filter {
                    eq("id", profileId)
                }
            }
        true
    }

    suspend fun deleteProfile(profileId: String): Boolean = withContext(Dispatchers.IO) {
        supabase.postgrest["profiles"]
            .delete {
                filter {
                    eq("id", profileId)
                }
            }
        true
    }
}
