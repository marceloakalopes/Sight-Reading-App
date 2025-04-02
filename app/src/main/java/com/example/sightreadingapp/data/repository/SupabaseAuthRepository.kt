package com.example.sightreadingapp.data.repository

import com.example.sightreadingapp.data.session.SessionManager
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo

/**
 * Repository handling authentication operations with Supabase.
 *
 * @param sessionManager The [SessionManager] to use for saving and loading sessions.
 */
class SupabaseAuthRepository(private val sessionManager: SessionManager) {

    private val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(io.github.jan.supabase.auth.Auth)
    }

    /**
     * Initializes the repository by importing any saved session and refreshing it.
     */
    suspend fun initialize() {
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

    /**
     * Refreshes the current session.
     */
    suspend fun refreshSession() {
        client.auth.refreshCurrentSession()
        client.auth.currentSessionOrNull()?.let { sessionManager.saveSession(it) }
    }

    /**
     * Retrieves the current user's email.
     */
    suspend fun getCurrentUserEmail(): String? {
        return client.auth.currentUserOrNull()?.email
    }

    /**
     * Signs up a user with the given email and password.
     *
     * @param email User email.
     * @param password User password.
     * @return The created [UserInfo].
     * @throws Exception if sign up fails.
     */
    suspend fun signUp(email: String, password: String): UserInfo {
        val user = client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        } ?: throw Exception("Sign up failed")

        client.auth.currentSessionOrNull()?.let { session ->
            sessionManager.saveSession(session)
        }

        return user
    }

    /**
     * Logs in a user with the given email and password.
     *
     * @param email User email.
     * @param password User password.
     * @return The logged-in [UserInfo].
     * @throws Exception if login fails.
     */
    suspend fun login(email: String, password: String): UserInfo {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        val currentUserInfo = client.auth.currentUserOrNull() ?: throw Exception("Login failed")

        client.auth.currentSessionOrNull()?.let { session ->
            sessionManager.saveSession(session)
        }

        return currentUserInfo
    }

    /**
     * Signs out the current user.
     */
    suspend fun signOut() {
        client.auth.signOut()
        sessionManager.clearSession()
    }
}
