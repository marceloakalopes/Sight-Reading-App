package com.example.sightreadingapp.session

import android.content.Context
import androidx.core.content.edit
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.json.Json

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    companion object {
        private const val SESSION_KEY = "session_key"
    }
    fun saveSession(session: UserSession) {
        val json = Json.encodeToString(session)
        prefs.edit { putString(SESSION_KEY, json) }
    }
    fun getSession(): UserSession? {
        val json = prefs.getString(SESSION_KEY, null) ?: return null
        return try {
            Json.decodeFromString<UserSession>(json)
        } catch (e: Exception) {
            null
        }
    }
    fun clearSession() {
        prefs.edit { remove(SESSION_KEY) }
    }
}
