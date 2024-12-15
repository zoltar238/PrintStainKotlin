package org.example.project.persistence.preferences

import androidx.datastore.preferences.core.stringPreferencesKey

object AuthPreferenceKeys {
    val USERNAME_KEY = stringPreferencesKey("username")
    val PASSWORD_KEY = stringPreferencesKey("password")
    val TOKEN_KEY = stringPreferencesKey("auth_token")
}