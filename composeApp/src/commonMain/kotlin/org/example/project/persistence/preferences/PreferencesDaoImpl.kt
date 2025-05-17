package org.example.project.persistence.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

object PreferencesDaoImpl : PreferencesDao {

    private lateinit var pref: DataStore<Preferences>

    // Initialize dataStore
    fun initPreferences(dataStoreInit: DataStore<Preferences>) {
        pref = dataStoreInit
    }

    // #######################################################
    // Save functionality
    // #######################################################

    // Generic method for saving data
    private suspend fun <T> saveData(key: Preferences.Key<T>, data: T) {
        pref.edit { preferences ->
            preferences[key] = data
        }
    }

    // Save username
    suspend fun saveUsername(username: String) = saveData(PreferenceKeys.USERNAME_KEY, username)

    // Save password
    suspend fun savePassword(password: String) = saveData(PreferenceKeys.PASSWORD_KEY, password)

    // Save token
    suspend fun saveToken(token: String) = saveData(PreferenceKeys.TOKEN_KEY, token)

    // Save dark mode
    suspend fun saveDarkMode(darkMode: Boolean) = saveData(PreferenceKeys.THEME_KEY, darkMode)

    // #######################################################
    // Read functionality
    // #######################################################

    // Generic method for reading data
    private suspend fun <T> getPreference(key: Preferences.Key<T>): T? {
        val preferences = pref.data.first()
        return preferences[key]
    }

    // Read username
    suspend fun getUsername(): String? = getPreference(PreferenceKeys.USERNAME_KEY)

    // Read password
    suspend fun getPassword(): String? = getPreference(PreferenceKeys.PASSWORD_KEY)

    // Read token
    suspend fun getToken(): String? = getPreference(PreferenceKeys.TOKEN_KEY)

    // Dark mode
    suspend fun getDarkMode(): Boolean? = getPreference(PreferenceKeys.THEME_KEY)


    // #######################################################
    // Delete functionality
    // #######################################################

    // Delete token
    suspend fun clearToken() {
        pref.edit { preferences ->
            preferences.remove(PreferenceKeys.TOKEN_KEY)
        }
    }

    // Delete all preferences
    override suspend fun deleteAllPreferences() {
        pref.edit { preferences ->
            preferences.clear()
        }
    }


    // #######################################################
    // App functionality
    // #######################################################
    override suspend fun saveUser(username: String, password: String, token: String) {
        saveUsername(username)
        savePassword(password)
        saveToken(token)
    }

}