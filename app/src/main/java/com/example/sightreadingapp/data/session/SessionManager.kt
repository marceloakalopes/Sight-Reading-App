package com.example.sightreadingapp.data.session

import android.content.Context
import androidx.core.content.edit
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.json.Json

/**
 * Manages saving and retrieving the user's session.
 */
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val SESSION_KEY = "session_key"
    }

    /**
     * Saves the given user session in shared preferences.
     *
     * @param session The user session to save.
     */
    fun saveSession(session: UserSession) {
        val json = Json.encodeToString(session)
        prefs.edit { putString(SESSION_KEY, json) }
    }

    /**
     * Retrieves the saved user session, if available.
     *
     * @return The saved [UserSession] or null if none exists or if parsing fails.
     */
    fun getSession(): UserSession? {
        val json = prefs.getString(SESSION_KEY, null) ?: return null
        return try {
            Json.decodeFromString<UserSession>(json)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Clears the saved user session.
     */
    fun clearSession() {
        prefs.edit { remove(SESSION_KEY) }
    }
}
