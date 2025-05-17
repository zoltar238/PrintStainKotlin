package org.example.project.persistence.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val USERNAME_KEY = stringPreferencesKey("username")
    val PASSWORD_KEY = stringPreferencesKey("password")
    val TOKEN_KEY = stringPreferencesKey("auth_token")
    val THEME_KEY = booleanPreferencesKey("theme")
}