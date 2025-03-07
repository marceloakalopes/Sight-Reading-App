package com.example.sightreadingapp.repository

import com.example.sightreadingapp.session.SessionManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SupabaseAuthRepository(private val sessionManager: SessionManager) {

    private val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://lhzquvsiudducczhvqrs.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxoenF1dnNpdWRkdWNjemh2cXJzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAxNzAwOTksImV4cCI6MjA1NTc0NjA5OX0.Jq-F1Am5EDS5eSd56MUeeiRmCD7NiUYhzgJw5TKhd1Q"
    ) {
        install(Auth)
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            sessionManager.getSession()?.let { session ->
                client.auth.importSession(session)
            }
            try {
                client.auth.refreshCurrentSession()
                client.auth.currentSessionOrNull()?.let { refreshed ->
                    sessionManager.saveSession(refreshed)
                }
            } catch (e: Exception) {
                sessionManager.clearSession()
            }
        }
    }

    suspend fun refreshSession() {
        client.auth.refreshCurrentSession()
        client.auth.currentSessionOrNull()?.let { sessionManager.saveSession(it) }
    }

    suspend fun getCurrentUserEmail(): String? {
        return client.auth.currentUserOrNull()?.email
    }

    suspend fun signUp(email: String, password: String): UserInfo {
        val user = client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        if (user == null) {
            throw Exception()
        }

        client.auth.currentSessionOrNull()?.let { session ->
            sessionManager.saveSession(session)
        }

        return user
    }

    suspend fun login(email: String, password: String): UserInfo {
        val res = client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        val currentUserInfo = client.auth.currentUserOrNull() ?: throw Exception()

        client.auth.currentSessionOrNull()?.let { session ->
            sessionManager.saveSession(session)
        }

        return currentUserInfo
    }

    suspend fun signOut() {
        client.auth.signOut()
        sessionManager.clearSession()
    }

}

