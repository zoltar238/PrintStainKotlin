package org.example.project.persistence.preferences

interface PreferencesManagerContract {
    suspend fun saveUser(username: String, password: String, token: String)

    suspend fun deleteAllPreferences()
}