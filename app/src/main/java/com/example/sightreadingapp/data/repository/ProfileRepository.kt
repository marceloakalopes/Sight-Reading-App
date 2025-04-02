package com.example.sightreadingapp.data.repository

import com.example.sightreadingapp.data.models.UserProfile
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository handling profile operations with Supabase.
 */
object ProfileRepository {

    // Create a Supabase client with the Postgrest plugin installed.
    private val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
    }

    /**
     * Retrieves profiles for the given parent.
     *
     * @param parentId The ID of the parent.
     * @return A list of profiles for the parent.
     */
    suspend fun getProfilesForParent(parentId: String): List<UserProfile> = withContext(Dispatchers.IO) {
        supabase.postgrest["profiles"]
            .select() {
                filter {
                    eq("parentid", parentId)
                }
            }
            .decodeList<UserProfile>()
    }

    /**
     * Creates a new profile for the given parent.
     *
     * @param parentId The ID of the parent.
     * @param kidName The name of the kid.
     * @return The newly created profile.
     */
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

    /**
     * Updates the score of a profile.
     *
     * @param profileId The ID of the profile.
     * @param newScore The new score.
     * @return `true` if the update was successful, `false` otherwise.
     */
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

    /**
     * Deletes a profile.
     *
     * @param profileId The ID of the profile.
     * @return `true` if the deletion was successful, `false` otherwise.
     */
    suspend fun deleteProfile(profileId: String): Boolean = withContext(Dispatchers.IO) {
        supabase.postgrest["profiles"]
            .delete {
                filter {
                    eq("id", profileId)
                }
            }
        true
    }

    /**
     * Retrieves all profiles.
     *
     * @return A list of all profiles.
     */
    suspend fun getAllProfiles(): List<UserProfile> = withContext(Dispatchers.IO) {
        supabase.postgrest["profiles"]
            .select() // Select all profiles.
            .decodeList<UserProfile>()
    }
}
